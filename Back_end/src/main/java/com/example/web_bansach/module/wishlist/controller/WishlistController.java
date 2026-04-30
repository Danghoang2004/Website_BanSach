package com.example.web_bansach.module.wishlist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.web_bansach.module.wishlist.dto.response.WishlistResponse;
import com.example.web_bansach.module.wishlist.service.WishlistService;

/**
 * Controller xử lý danh sách yêu thích (wishlist)
 */
@RestController
@RequestMapping("/user/wishlist")
@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    /**
     * Thêm sách vào danh sách yêu thích
     * POST /user/wishlist/books/{bookId}
     */
    @PostMapping("/books/{bookId}")
    public ResponseEntity<WishlistResponse> addToWishlist(
            Authentication auth,
            @PathVariable Long bookId) {
        String username = auth.getName();
        WishlistResponse wishlist = wishlistService.addToWishlist(username, bookId);
        return ResponseEntity.status(HttpStatus.CREATED).body(wishlist);
    }

    /**
     * Xóa sách khỏi danh sách yêu thích
     * DELETE /user/wishlist/books/{bookId}
     */
    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<?> removeFromWishlist(
            Authentication auth,
            @PathVariable Long bookId) {
        String username = auth.getName();
        wishlistService.removeFromWishlist(username, bookId);
        return ResponseEntity.ok("Sách đã được xóa khỏi danh sách yêu thích");
    }

    /**
     * Kiểm tra sách có trong danh sách yêu thích không
     * GET /user/wishlist/books/{bookId}/check
     */
    @GetMapping("/books/{bookId}/check")
    public ResponseEntity<?> checkInWishlist(
            Authentication auth,
            @PathVariable Long bookId) {
        String username = auth.getName();
        boolean isInWishlist = wishlistService.isInWishlist(username, bookId);
        return ResponseEntity.ok(java.util.Map.of("isInWishlist", isInWishlist));
    }

    /**
     * Lấy danh sách yêu thích của user
     * GET /user/wishlist?page=0&size=10
     */
    @GetMapping
    public ResponseEntity<Page<WishlistResponse>> getMyWishlist(
            Authentication auth,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        String username = auth.getName();
        Page<WishlistResponse> wishlist = wishlistService.getMyWishlist(username, page, size);
        return ResponseEntity.ok(wishlist);
    }

    /**
     * Lấy số lượng sách trong danh sách yêu thích
     * GET /user/wishlist/count
     */
    @GetMapping("/count")
    public ResponseEntity<?> getWishlistCount(Authentication auth) {
        String username = auth.getName();
        long count = wishlistService.getWishlistCount(username);
        return ResponseEntity.ok(java.util.Map.of("count", count));
    }
}
