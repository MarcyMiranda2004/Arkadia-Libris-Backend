package com.example.capstone.arkadia.libris.dto.request.stock;

import com.example.capstone.arkadia.libris.enumerated.ProductType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAndUpdateCategoryRequestDto {
    @NotBlank(message = "Il nome della categoria non può essere vuoto")
    private String name;
    @NotNull(message = "Il tipo di categoria non può essere nullo")
    private ProductType productCategoryType ;
}
