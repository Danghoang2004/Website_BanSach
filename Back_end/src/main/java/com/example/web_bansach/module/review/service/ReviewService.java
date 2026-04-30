package com.example.web_bansach.module.review.service;

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
import com.example.web_bansach.module.review.dto.request.CreateReviewRequest;
import com.example.web_bansach.module.review.dto.response.ReviewResponse;
import com.example.web_bansach.module.review.entity.Review;
import com.example.web_bansach.module.review.repository.ReviewRepository;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.repository.UserRepository;

/**
 * Service xử lý đánh giá sách
 */
@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    /**
     * Tạo đánh giá mới (user)
     */
    @Transactional(rollbackFor = Exception.class)
    public ReviewResponse createReview(String username, CreateReviewRequest request) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));

        // Kiểm tra user đã review sách này chưa
        if (reviewRepository.findByUserIdAndBookId(user.getId(), book.getId()).isPresent()) {
            throw new BusinessException("Bạn đã đánh giá sách này rồi");
        }

        Review review = new Review();
        review.setUser(user);
        review.setBook(book);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);
        return mapToResponse(savedReview);
    }

    /**
     * Cập nhật đánh giá (user - chỉ đánh giá của chính mình)
     */
    @Transactional(rollbackFor = Exception.class)
    public ReviewResponse updateReview(String username, Long reviewId, CreateReviewRequest request) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá"));

        // Kiểm tra đánh giá có thuộc user này không
        if (!review.getUser().getId().equals(user.getId())) {
            throw new BusinessException("Bạn không có quyền sửa đánh giá này");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updatedReview = reviewRepository.save(review);
        return mapToResponse(updatedReview);
    }

    /**
     * Xóa đánh giá (user - chỉ đánh giá của chính mình)
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteReview(String username, Long reviewId) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá"));

        // Kiểm tra đánh giá có thuộc user này không
        if (!review.getUser().getId().equals(user.getId())) {
            throw new BusinessException("Bạn không có quyền xóa đánh giá này");
        }

        reviewRepository.delete(review);
    }

    /**
     * Lấy đánh giá của user cho một sách
     */
    @Transactional(readOnly = true)
    public ReviewResponse getMyReview(String username, Long bookId) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        Review review = reviewRepository.findByUserIdAndBookId(user.getId(), bookId)
                .orElse(null);

        return review != null ? mapToResponse(review) : null;
    }

    /**
     * Lấy tất cả đánh giá của một sách (user)
     */
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByBook(Long bookId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository.findByBookId(bookId, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Lấy tất cả đánh giá của một user (admin)
     */
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository.findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Lấy chi tiết một đánh giá
     */
    @Transactional(readOnly = true)
    public ReviewResponse getReviewDetail(Long reviewId) {
        Review review = reviewRepository.findByIdWithJoin(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá"));
        return mapToResponse(review);
    }

    /**
     * Xóa đánh giá (admin)
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteReviewAdmin(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá"));
        reviewRepository.delete(review);
    }

    /**
     * Lấy rating trung bình của sách
     */
    @Transactional(readOnly = true)
    public Double getAverageRating(Long bookId) {
        return reviewRepository.getAverageRatingByBookId(bookId);
    }

    /**
     * Lấy số lượng đánh giá của sách
     */
    @Transactional(readOnly = true)
    public long getReviewCount(Long bookId) {
        return reviewRepository.countByBookId(bookId);
    }

    private ReviewResponse mapToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        if (review.getUser() != null) {
            response.setUserId(review.getUser().getId());
            response.setUserName(review.getUser().getFullName() != null
                    ? review.getUser().getFullName()
                    : review.getUser().getUsername());
        }
        if (review.getBook() != null) {
            response.setBookId(review.getBook().getId());
            response.setBookTitle(review.getBook().getTitle());
        }
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }
}
