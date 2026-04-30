package com.example.web_bansach.module.cart.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class CartResponse {
    private Long cartId;
    private Integer totalItems;
    private BigDecimal totalAmount;
    private List<CartItemResponse> items;
}
