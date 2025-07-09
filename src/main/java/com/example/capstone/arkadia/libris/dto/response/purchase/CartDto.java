package com.example.capstone.arkadia.libris.dto.response.purchase;

import lombok.Data;

import java.util.List;

@Data
public class CartDto {
    private Long CartId;
    private List<CartItemDto> items;
}
