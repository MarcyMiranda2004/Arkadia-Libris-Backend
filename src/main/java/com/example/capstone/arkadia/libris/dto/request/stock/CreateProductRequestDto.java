package com.example.capstone.arkadia.libris.dto.request.stock;

import com.example.capstone.arkadia.libris.enumerated.ProductType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateProductRequestDto {
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

    @NotNull(message = "Devi specificare il tipo di prodotto")
    private ProductType productType;

    @NotEmpty(message = "Il prodotto deve avere almeno una categoria")
    private List<@NotBlank(message = "Ogni categoria non può essere vuota") String> categories;

    private List<@NotBlank(message = "Ogni URL immagine non può essere vuoto") String> images = new ArrayList<>();

    @NotNull(message = "Devi specificare la quantità iniziale di magazzino")
    @Min(value = 0, message = "La quantità iniziale non può essere negativa")
    private Integer initialStock;
}
