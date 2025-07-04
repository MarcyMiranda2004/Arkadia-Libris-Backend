package com.example.capstone.arkadia.libris.dto.response.stock;

import lombok.Data;

@Data
public class InventoryItemDto {
    private Long productId;
    private String title;
    private int quantity;
}
