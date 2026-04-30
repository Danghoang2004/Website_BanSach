package com.example.web_bansach.module.order.entity;

/**
 * Trạng thái đơn hàng
 * PENDING - Chờ xác nhận
 * CONFIRMED - Đã xác nhận
 * SHIPPING - Đang giao hàng
 * COMPLETED - Hoàn thành
 * CANCELLED - Đã hủy
 */
public enum OrderStatus {
    PENDING, // Chờ xác nhận
    CONFIRMED, // Đã xác nhận
    SHIPPING, // Đang giao hàng
    COMPLETED, // Hoàn thành
    CANCELLED // Đã hủy
}
