package com.example.web_bansach.module.order.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.cart.entity.Cart;
import com.example.web_bansach.module.cart.entity.CartItem;
import com.example.web_bansach.module.cart.repository.CartItemRepository;
import com.example.web_bansach.module.cart.repository.CartRepository;
import com.example.web_bansach.module.cart.service.CartService;
import com.example.web_bansach.module.inventory.entity.Inventory;
import com.example.web_bansach.module.inventory.repository.InventoryRepository;
import com.example.web_bansach.module.order.dto.request.CreateOrderRequest;
import com.example.web_bansach.module.order.dto.response.OrderItemResponse;
import com.example.web_bansach.module.order.dto.response.OrderResponse;
import com.example.web_bansach.module.order.entity.Order;
import com.example.web_bansach.module.order.entity.OrderItem;
import com.example.web_bansach.module.order.entity.OrderStatus;
import com.example.web_bansach.module.order.repository.OrderItemRepository;
import com.example.web_bansach.module.order.repository.OrderRepository;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.repository.UserRepository;
import com.example.web_bansach.module.voucher.service.VoucherService;

@Service
public class OrderUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private VoucherService voucherService;

    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createOrder(String username, CreateOrderRequest request) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        Cart cart = cartService.getOrCreateCart(username);
        List<CartItem> cartItems = cartItemRepository.findByCartIdWithBook(cart.getId());
        if (cartItems.isEmpty()) {
            throw new BusinessException("Giỏ hàng trống, không thể tạo đơn");
        }

        // Kiểm tra tồn kho cho tất cả items trước khi tạo order
        for (CartItem item : cartItems) {
            Inventory inventory = inventoryRepository.findByBookId(item.getBook().getId())
                    .orElseThrow(() -> new BusinessException(
                            "Không tìm thấy bản ghi tồn kho cho sách: " + item.getBook().getTitle()));

            if (inventory.getQuantity() == null || inventory.getQuantity() <= 0) {
                throw new BusinessException(
                        "Sách '" + item.getBook().getTitle() + "' đã hết hàng");
            }

            if (inventory.getQuantity() < item.getQuantity()) {
                throw new BusinessException(
                        "Số lượng sách '" + item.getBook().getTitle() + "' không đủ. "
                                + "Yêu cầu: " + item.getQuantity() + ", Hiện còn: " + inventory.getQuantity());
            }
        }

        BigDecimal itemsTotal = cartItems.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shippingFee = request.getShippingFee() == null ? BigDecimal.ZERO : request.getShippingFee();
        BigDecimal totalAmount = itemsTotal.add(shippingFee);
        BigDecimal voucherDiscount = BigDecimal.ZERO;

        // Xử lý voucher nếu có
        if (request.getVoucherCode() != null && !request.getVoucherCode().trim().isEmpty()) {
            try {
                var voucherResponse = voucherService.getVoucherByCode(request.getVoucherCode());
                
                // Tính tiền giảm: (discountPercent / 100) * itemsTotal, nhưng không vượt quá maxDiscount
                BigDecimal discountAmount = itemsTotal.multiply(new BigDecimal(voucherResponse.getDiscountPercent()))
                        .divide(new BigDecimal(100), 2, java.math.RoundingMode.HALF_UP);
                
                if (discountAmount.compareTo(voucherResponse.getMaxDiscount()) > 0) {
                    discountAmount = voucherResponse.getMaxDiscount();
                }

                voucherDiscount = discountAmount;
                totalAmount = totalAmount.subtract(voucherDiscount);

                // Đảm bảo tổng không âm
                if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
                    totalAmount = BigDecimal.ZERO;
                }
            } catch (ResourceNotFoundException | BusinessException e) {
                // Nếu voucher không hợp lệ, vẫn tạo order mà không giảm giá
                // Có thể log warning ở đây nếu cần
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setReceiverName(request.getReceiverName().trim());
        order.setReceiverPhone(request.getReceiverPhone().trim());
        order.setShippingAddress(request.getShippingAddress().trim());
        order.setShippingMethod(request.getShippingMethod());
        order.setShippingFee(shippingFee);
        order.setVoucherCode(request.getVoucherCode() != null ? request.getVoucherCode().toUpperCase() : null);
        order.setVoucherDiscount(voucherDiscount);
        order.setTotalAmount(totalAmount);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // Tạo OrderItems từ CartItems và giảm Inventory
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItemRepository.save(orderItem);

            // Giảm Inventory quantity
            Inventory inventory = inventoryRepository.findByBookId(cartItem.getBook().getId()).get();
            inventory.setQuantity(inventory.getQuantity() - cartItem.getQuantity());
            inventoryRepository.save(inventory);
        }

        // Sử dụng voucher nếu đã áp dụng thành công
        if (request.getVoucherCode() != null && !request.getVoucherCode().trim().isEmpty() 
                && voucherDiscount.compareTo(BigDecimal.ZERO) > 0) {
            try {
                voucherService.useVoucher(request.getVoucherCode());
            } catch (Exception e) {
                // Log warning nếu cần, nhưng không throw exception vì order đã tạo
            }
        }

        cartService.clearCart(username);
        return mapToOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getMyOrders(String username, int page, int size) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        return orderRepository.findByUserId(user.getId(), pageable)
                .map(this::mapToOrderResponse);
    }

    @Transactional(readOnly = true)
    public OrderResponse getMyOrderDetail(String username, Long orderId) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        Order order = orderRepository.findByIdAndUserId(orderId, user.getId());
        if (order == null) {
            throw new ResourceNotFoundException("Không tìm thấy đơn hàng");
        }

        return mapToOrderResponse(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelMyOrder(String username, Long orderId) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        Order order = orderRepository.findByIdAndUserId(orderId, user.getId());
        if (order == null) {
            throw new ResourceNotFoundException("Không tìm thấy đơn hàng");
        }

        if (order.getStatus() == OrderStatus.SHIPPING) {
            throw new BusinessException("Không thể hủy đơn hàng đang giao");
        }
        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new BusinessException("Không thể hủy đơn hàng đã hoàn thành");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("Đơn hàng đã bị hủy trước đó");
        }

        // Hoàn lại inventory
        List<OrderItem> orderItems = orderItemRepository.findByOrderIdWithBook(order.getId());
        for (OrderItem item : orderItems) {
            Inventory inventory = inventoryRepository.findByBookId(item.getBook().getId()).get();
            inventory.setQuantity(inventory.getQuantity() + item.getQuantity());
            inventoryRepository.save(inventory);
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setReceiverName(order.getReceiverName());
        response.setReceiverPhone(order.getReceiverPhone());
        response.setShippingAddress(order.getShippingAddress());
        response.setVoucherCode(order.getVoucherCode());
        response.setVoucherDiscount(order.getVoucherDiscount());
        response.setOrderDate(order.getOrderDate());

        List<OrderItemResponse> items = orderItemRepository.findByOrderIdWithBook(order.getId())
                .stream()
                .map(item -> {
                    OrderItemResponse itemResponse = new OrderItemResponse();
                    itemResponse.setBookId(item.getBook().getId());
                    itemResponse.setBookTitle(item.getBook().getTitle());
                    itemResponse.setQuantity(item.getQuantity());
                    itemResponse.setPrice(item.getPrice());
                    return itemResponse;
                })
                .collect(Collectors.toList());

        response.setItems(items);
        return response;
    }
}
