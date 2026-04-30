package com.example.web_bansach.module.category.dto.response;

import lombok.Data;

@Data
public class CategoryResponse {
    private Long id;
    private String categoryName;
    private String description;
    private Integer bookCount; // optional
}
