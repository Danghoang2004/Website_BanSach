package com.example.web_bansach.module.order.dto.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemResponse {
    private Long bookId;
    private String bookTitle;
    private Integer quantity;
    private BigDecimal price;
}
