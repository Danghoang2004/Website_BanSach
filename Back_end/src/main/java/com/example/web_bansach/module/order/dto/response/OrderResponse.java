package com.example.web_bansach.module.order.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.web_bansach.module.order.entity.OrderStatus;

import lombok.Data;

@Data
public class OrderResponse {
    private Long id;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;
    private String voucherCode; // Mã voucher áp dụng
    private BigDecimal voucherDiscount; // Số tiền giảm từ voucher
    private LocalDateTime orderDate;
    private List<OrderItemResponse> items;
}
