package com.example.web_bansach.module.wishlist.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.book.entity.Book;
import com.example.web_bansach.module.book.repository.BookRepository;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.repository.UserRepository;
import com.example.web_bansach.module.wishlist.dto.response.WishlistResponse;
import com.example.web_bansach.module.wishlist.entity.Wishlist;
import com.example.web_bansach.module.wishlist.entity.WishlistId;
import com.example.web_bansach.module.wishlist.repository.WishlistRepository;

/**
 * Service quản lý danh sách yêu thích
 */
@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    /**
     * Thêm sách vào danh sách yêu thích
     */
    @Transactional(rollbackFor = Exception.class)
    public WishlistResponse addToWishlist(String username, Long bookId) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));

        // Kiểm tra sách đã bị xóa chưa
        if (book.getDeletedAt() != null) {
            throw new BusinessException("Sách này không còn khả dụng");
        }

        // Kiểm tra sách đã trong wishlist chưa
        if (wishlistRepository.existsByUserIdAndBookId(user.getId(), book.getId())) {
            throw new BusinessException("Sách này đã có trong danh sách yêu thích của bạn");
        }

        WishlistId id = new WishlistId(user.getId(), book.getId());
        Wishlist wishlist = new Wishlist();
        wishlist.setId(id);
        wishlist.setUser(user);
        wishlist.setBook(book);
        wishlist.setCreatedAt(LocalDateTime.now());

        Wishlist savedWishlist = wishlistRepository.save(wishlist);
        return mapToResponse(savedWishlist);
    }

    /**
     * Xóa sách khỏi danh sách yêu thích
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeFromWishlist(String username, Long bookId) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        if (!wishlistRepository.existsByUserIdAndBookId(user.getId(), bookId)) {
            throw new BusinessException("Sách này không có trong danh sách yêu thích");
        }

        wishlistRepository.deleteByIdUserIdAndIdBookId(user.getId(), bookId);
    }

    /**
     * Kiểm tra sách có trong danh sách yêu thích không
     */
    @Transactional(readOnly = true)
    public boolean isInWishlist(String username, Long bookId) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            return false;
        }
        return wishlistRepository.existsByUserIdAndBookId(user.getId(), bookId);
    }

    /**
     * Lấy danh sách yêu thích của user (paged)
     */
    @Transactional(readOnly = true)
    public Page<WishlistResponse> getMyWishlist(String username, int page, int size) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return wishlistRepository.findByIdUserId(user.getId(), pageable)
                .map(this::mapToResponse);
    }

    /**
     * Lấy số lượng sách trong danh sách yêu thích
     */
    @Transactional(readOnly = true)
    public long getWishlistCount(String username) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        return wishlistRepository.countByUserId(user.getId());
    }

    /**
     * Xóa toàn bộ danh sách yêu thích (admin - khi xóa user)
     */
    @Transactional(rollbackFor = Exception.class)
    public void clearWishlist(Long userId) {
        var wishlists = wishlistRepository.findAllByIdUserId(userId);
        wishlistRepository.deleteAll(wishlists);
    }

    private WishlistResponse mapToResponse(Wishlist wishlist) {
        WishlistResponse response = new WishlistResponse();
        if (wishlist.getBook() != null) {
            Book book = wishlist.getBook();
            response.setBookId(book.getId());
            response.setBookTitle(book.getTitle());
            response.setBookCoverImage(book.getCoverImage());
            response.setBookDescription(book.getDescription());
            response.setBookPublisher(book.getPublisher());
            response.setBookPrice(book.getPrice());

            if (book.getAuthor() != null) {
                response.setBookAuthor(book.getAuthor().getAuthorName());
            }
        }
        response.setAddedAt(wishlist.getCreatedAt());
        return response;
    }
}
