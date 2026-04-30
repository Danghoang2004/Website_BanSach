package com.example.web_bansach.module.book.dto.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class BookResponse {
    private Long id;
    private String title;
    private BigDecimal price;
    private String description;
    private String coverImage;
    private String authorName;
    private String categoryName;
    private Integer discountPercent;
}
