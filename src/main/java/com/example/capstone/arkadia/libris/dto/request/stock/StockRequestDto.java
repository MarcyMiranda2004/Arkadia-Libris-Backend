package com.example.capstone.arkadia.libris.dto.request.stock;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockRequestDto {
    @NotNull
    @Min(value = 1, message = "La quantità deve essere almeno 1")
    private Integer quantity;
}
