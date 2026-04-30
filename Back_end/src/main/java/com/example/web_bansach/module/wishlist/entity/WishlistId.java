package com.example.web_bansach.module.wishlist.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WishlistId implements Serializable {

    private Long userId; // ID người dùng
    private Long bookId; // ID sách
}
