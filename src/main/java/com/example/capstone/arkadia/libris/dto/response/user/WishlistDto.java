package com.example.capstone.arkadia.libris.dto.response.user;

import lombok.Data;

import java.util.List;

@Data
public class WishlistDto {
    private Long wishlistId;
    private List<WishlistItemDto> items;
}
