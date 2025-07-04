package com.example.capstone.arkadia.libris.dto.request.stock;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class UpdateProductRequestDto {
    @NotBlank(message = "Il prodotto deve avere un nome")
    private String title;

    private String isbn;

    @NotBlank(message = "Il prodotto deve avere un autore")
    private String author;

    @NotBlank(message = "Il prodotto deve avere un editore")
    private String publisher;

    @NotBlank(message = "Il prodotto deve avere una descrizione")
    private String description;

    @NotNull(message = "Il prezzo non può essere nullo")
    @Positive(message = "Il prezzo deve essere maggiore di zero")
    private Double price;

    @NotEmpty(message = "Il prodotto deve avere almeno una categoria")
    private List<@NotBlank(message = "Ogni categoria non può essere vuota") String> categories;

    private List<@NotBlank(message = "Ogni URL immagine non può essere vuoto") String> images;

    // rimane senza stock qui, lo gestiamo solo in inventory
}
