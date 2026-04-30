package com.example.web_bansach.module.wishlist.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.web_bansach.module.wishlist.entity.Wishlist;
import com.example.web_bansach.module.wishlist.entity.WishlistId;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, WishlistId> {

    // Find wishlist item by user and book
    Optional<Wishlist> findByIdUserIdAndIdBookId(Long userId, Long bookId);

    // Find all wishlist items for a user (paged)
    @Query("SELECT w FROM Wishlist w JOIN FETCH w.book WHERE w.id.userId = :userId ORDER BY w.createdAt DESC")
    Page<Wishlist> findByIdUserId(@Param("userId") Long userId, Pageable pageable);

    // Find all wishlist items for a user (unpaged - for quick checks)
    @Query("SELECT w FROM Wishlist w WHERE w.id.userId = :userId")
    List<Wishlist> findAllByIdUserId(@Param("userId") Long userId);

    // Check if book is in user's wishlist
    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END FROM Wishlist w WHERE w.id.userId = :userId AND w.id.bookId = :bookId")
    boolean existsByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    // Count wishlist items for a user
    @Query("SELECT COUNT(w) FROM Wishlist w WHERE w.id.userId = :userId")
    long countByUserId(@Param("userId") Long userId);

    // Delete wishlist item
    void deleteByIdUserIdAndIdBookId(Long userId, Long bookId);
}



