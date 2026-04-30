package com.example.web_bansach.module.review.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReviewResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long bookId;
    private String bookTitle;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
