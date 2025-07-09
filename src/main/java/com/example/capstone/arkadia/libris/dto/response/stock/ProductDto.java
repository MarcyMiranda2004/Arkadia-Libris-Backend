package com.example.capstone.arkadia.libris.dto.response.stock;

import lombok.Data;
import java.util.List;

@Data
public class ProductDto {
    private Long id;
    private String title;
    private String isbn;
    private String author;
    private String publisher;
    private String description;
    private double price;
    private List<String> categories;
    private List<String> images;
    private int stock;
}
