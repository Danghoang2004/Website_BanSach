package com.example.web_bansach.module.cart.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.module.cart.dto.response.CartItemResponse;
import com.example.web_bansach.module.cart.dto.response.CartResponse;
import com.example.web_bansach.module.cart.dto.request.AddToCartRequest;
import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.book.entity.Book;
import com.example.web_bansach.module.cart.entity.Cart;
import com.example.web_bansach.module.cart.entity.CartItem;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.book.repository.BookRepository;
import com.example.web_bansach.module.cart.repository.CartItemRepository;
import com.example.web_bansach.module.cart.repository.CartRepository;
import com.example.web_bansach.module.user.repository.UserRepository;
import com.example.web_bansach.module.inventory.repository.InventoryRepository;
import com.example.web_bansach.module.inventory.entity.Inventory;

/**
 * Service xử lý nghiệp vụ giỏ hàng
 */
@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    /**
     * Lấy hoặc tạo giỏ hàng cho user
     */
    @Transactional
    public Cart getOrCreateCart(String username) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setCreatedAt(LocalDateTime.now());
                    cart.setUpdatedAt(LocalDateTime.now());
                    return cartRepository.save(cart);
                });
    }

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    @Transactional(rollbackFor = Exception.class)
    public CartResponse addToCart(String username, AddToCartRequest request) {
        // Lấy giỏ hàng
        Cart cart = getOrCreateCart(username);
        // Kiểm tra sách tồn tại
        Book book = bookRepository.findByIdWithJoin(request.getBookId());
        if (book == null) {
            throw new ResourceNotFoundException("Không tìm thấy sách");
        }
        // Kiểm tra sách đã bị xóa chưa
        if (book.getDeletedAt() != null) {
            throw new BusinessException("Sách này không còn khả dụng");
        }
        // Kiểm tra tồn kho
        Inventory inventory = inventoryRepository.findByBookId(book.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi tồn kho"));
        if (inventory.getQuantity() == null || inventory.getQuantity() <= 0) {
            throw new BusinessException("Sách này hiện đã hết hàng");
        }
        if (inventory.getQuantity() < request.getQuantity()) {
            throw new BusinessException(
                    "Số lượng sách không đủ. Hiện còn: " + inventory.getQuantity() + " cuốn");
        }

        // Kiểm tra xem sách đã có trong giỏ chưa
        CartItem cartItem = cartItemRepository
                .findByCartIdAndBookId(cart.getId(), book.getId())
                .orElse(null);
        if (cartItem != null) {
            // Kiểm tra số lượng thêm không vượt quá tồn kho
            int totalQty = cartItem.getQuantity() + request.getQuantity();
            if (totalQty > inventory.getQuantity()) {
                throw new BusinessException(
                        "Tổng số lượng vượt quá tồn kho. Hiện còn: " + inventory.getQuantity() + " cuốn");
            }
            // Nếu đã có thì tăng số lượng
            cartItem.setQuantity(totalQty);
        } else {
            // Nếu chưa có thì tạo mới
            BigDecimal price = calculatePrice(book);
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setBook(book);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(price);
        }
        cartItemRepository.save(cartItem);
        // Cập nhật thời gian
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return getCart(username);
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ
     */
    @Transactional(rollbackFor = Exception.class)
    public CartResponse updateCartItem(String username, Long itemId, Integer quantity) {
        Cart cart = getOrCreateCart(username);
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm trong giỏ hàng"));
        // Kiểm tra item có thuộc giỏ của user không
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BusinessException("Sản phẩm không thuộc giỏ hàng của bạn");
        }
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        return getCart(username);
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    @Transactional(rollbackFor = Exception.class)
    public CartResponse removeCartItem(String username, Long itemId) {
        Cart cart = getOrCreateCart(username);
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm trong giỏ hàng"));
        // Kiểm tra item có thuộc giỏ của user không
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BusinessException("Sản phẩm không thuộc giỏ hàng của bạn");
        }
        cartItemRepository.delete(cartItem);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        return getCart(username);
    }

    /**
     * Xem giỏ hàng
     */
    @Transactional(readOnly = true)
    public CartResponse getCart(String username) {
        Cart cart = getOrCreateCart(username);
        // Lấy danh sách item với JOIN FETCH
        List<CartItem> items = cartItemRepository.findByCartIdWithBook(cart.getId());
        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setTotalItems(items.size());
        List<CartItemResponse> itemResponses = items.stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());
        BigDecimal totalAmount = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setItems(itemResponses);
        response.setTotalAmount(totalAmount);
        return response;
    }

    /**
     * Xóa toàn bộ giỏ hàng (dùng khi checkout)
     */
    @Transactional(rollbackFor = Exception.class)
    public void clearCart(String username) {
        Cart cart = getOrCreateCart(username);
        cartItemRepository.deleteByCartId(cart.getId());
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    /**
     * Map CartItem sang CartItemResponse
     */
    private CartItemResponse mapToCartItemResponse(CartItem item) {
        CartItemResponse response = new CartItemResponse();
        response.setId(item.getId());
        response.setBookId(item.getBook().getId());
        response.setBookTitle(item.getBook().getTitle());
        response.setBookCoverImage(item.getBook().getCoverImage());
        response.setBookPrice(item.getBook().getPrice());
        // Tính giá sau giảm giá
        BigDecimal priceAfterDiscount = calculatePrice(item.getBook());
        Integer discountPercent = (item.getBook().getDiscount() != null
                && item.getBook().getDiscount().getIsActive())
                        ? item.getBook().getDiscount().getDiscountPercent()
                        : null;
        response.setDiscountPercent(discountPercent);
        response.setPriceAfterDiscount(priceAfterDiscount);
        response.setQuantity(item.getQuantity());
        // Tính thành tiền
        BigDecimal subtotal = priceAfterDiscount.multiply(new BigDecimal(item.getQuantity()));
        response.setSubtotal(subtotal);
        return response;
    }

    /**
     * Tính giá sau giảm giá
     */
    private BigDecimal calculatePrice(Book book) {
        BigDecimal price = book.getPrice();
        if (book.getDiscount() != null && book.getDiscount().getIsActive()) {
            BigDecimal discountPercent = new BigDecimal(book.getDiscount().getDiscountPercent());
            BigDecimal discountAmount = price.multiply(discountPercent).divide(new BigDecimal(100));
            price = price.subtract(discountAmount);
        }
        return price;
    }
}
