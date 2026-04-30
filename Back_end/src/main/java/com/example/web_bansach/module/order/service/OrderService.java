package com.example.web_bansach.module.order.service;

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

import com.example.web_bansach.module.order.dto.response.OrderItemResponse;
import com.example.web_bansach.module.order.dto.response.OrderResponse;
import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.inventory.entity.Inventory;
import com.example.web_bansach.module.inventory.repository.InventoryRepository;
import com.example.web_bansach.module.order.entity.Order;
import com.example.web_bansach.module.order.entity.OrderItem;
import com.example.web_bansach.module.order.entity.OrderStatus;
import com.example.web_bansach.module.order.repository.OrderItemRepository;
import com.example.web_bansach.module.order.repository.OrderRepository;

/**
 * Service xử lý nghiệp vụ Order
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    /**
     * Lấy tất cả đơn hàng (Admin)
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new BusinessException("Tham số phân trang không hợp lệ");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> orderPage = orderRepository.findAll(pageable);

        return orderPage.map(this::mapToOrderResponse);
    }

    /**
     * Lấy chi tiết đơn hàng
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderDetail(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        return mapToOrderResponse(order);
    }

    /**
     * Cập nhật trạng thái đơn hàng (Admin)
     */
    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        // Validate trạng thái
        if (status == null) {
            throw new BusinessException("Trạng thái không hợp lệ");
        }

        // Rule nghiệp vụ: Không cập nhật đơn đã hủy hoặc hoàn thành
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("Đơn hàng đã bị hủy, không thể cập nhật");
        }

        if (order.getStatus() == OrderStatus.COMPLETED && status != OrderStatus.COMPLETED) {
            throw new BusinessException("Đơn hàng đã hoàn thành, không thể thay đổi trạng thái");
        }

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        Order updatedOrder = orderRepository.save(order);
        return mapToOrderResponse(updatedOrder);
    }

    /**
     * Hủy đơn hàng
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        // Không thể hủy đơn đang giao hoặc đã hoàn thành
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

    /**
     * Map Order sang OrderResponse
     * Sử dụng JOIN FETCH query để tránh N+1 problem
     */
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

        // Lấy danh sách OrderItem với JOIN FETCH (tránh N+1)
        List<OrderItem> orderItems = orderItemRepository.findByOrderIdWithBook(order.getId());

        List<OrderItemResponse> items = orderItems.stream()
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
