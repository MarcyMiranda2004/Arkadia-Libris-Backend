package com.example.capstone.arkadia.libris.dto.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToWishlistRequestDto {
    @NotNull(message = "Devi specificare l'id del prodotto")
    private Long productId;
}
