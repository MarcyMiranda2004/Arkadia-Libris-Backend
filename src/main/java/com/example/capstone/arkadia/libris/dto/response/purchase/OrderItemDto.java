package com.example.capstone.arkadia.libris.dto.response.purchase;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long productId;
    private String productName;
    private int quantity;
    private double Price;
}
