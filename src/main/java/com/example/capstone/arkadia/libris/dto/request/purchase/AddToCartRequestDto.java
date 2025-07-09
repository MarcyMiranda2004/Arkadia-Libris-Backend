package com.example.capstone.arkadia.libris.dto.request.purchase;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToCartRequestDto {
    @NotNull(message = "inserire l'id del prodotto")
    private Long productId;

    @Min(value = 1, message = "la quantità deve essere almeno 1")
    private int quantity;
}
