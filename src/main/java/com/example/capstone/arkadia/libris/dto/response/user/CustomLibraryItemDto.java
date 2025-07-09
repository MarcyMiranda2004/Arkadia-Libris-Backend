package com.example.capstone.arkadia.libris.dto.response.user;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CustomLibraryItemDto {
    private Long id;
    private String title;
    private String category;
    private List<String> genres;
    private String author;
    private String publisher;
    private LocalDate publicationDate;
    private String description;
    private String notes;
    private List<String> imageUrls;
}
