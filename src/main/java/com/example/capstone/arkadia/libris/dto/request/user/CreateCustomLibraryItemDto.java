package com.example.capstone.arkadia.libris.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateCustomLibraryItemDto {
    @NotBlank(message = "Devi dare un titolo al tuo elemento")
    private String title;

    @NotBlank(message = "Devi specificare il tipo di elemento")
    private String category;

    @NotEmpty(message = "Devi specificare almeno un genere")
    private List<@NotBlank(message = "Ogni genere non può essere vuoto") String> genres;

    private String author;
    private String publisher;
    private LocalDate publicationDate;

    @Size(max = 2000, message = "La descrizione può avere al massimo 2000 caratteri")
    private String description;

    @Size(max = 1000, message = "Le note possono avere al massimo 1000 caratteri")
    private String notes;

    private List<String> imageUrls;
}
