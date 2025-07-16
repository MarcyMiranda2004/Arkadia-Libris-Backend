package com.example.capstone.arkadia.libris.dto.response.user;

import lombok.Data;

import java.util.List;

@Data
public class WishlistItemDto {
    private Long productId;
    private String productName;
    private List<String> imageUrls;
    private double price;
}
