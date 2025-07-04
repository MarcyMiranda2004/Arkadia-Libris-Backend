package com.example.capstone.arkadia.libris.dto.response.stock;

import com.example.capstone.arkadia.libris.enumerated.ProductType;
import lombok.Data;

@Data
public class CategoryDto {
    private Long id;
    private String name;
    private ProductType productCategoryType;
}
