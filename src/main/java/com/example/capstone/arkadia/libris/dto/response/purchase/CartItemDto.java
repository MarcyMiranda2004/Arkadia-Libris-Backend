package com.example.capstone.arkadia.libris.dto.response.purchase;

import lombok.Data;

import java.util.List;

@Data
public class CartItemDto {
    private Long productId;
    private String productName;
    private List<String> imageUrls;
    private int quantity;
    private double price;
}
