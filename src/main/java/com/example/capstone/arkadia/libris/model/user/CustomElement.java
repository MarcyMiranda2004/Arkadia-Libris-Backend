package com.example.capstone.arkadia.libris.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "custom_elements")
public class CustomElement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "devi dare un titolo al tuo elemento")
    private String title;

    @NotBlank(message = "Devi specificare il tipo di elemento")
    private String category;

    @ElementCollection
    @CollectionTable(name = "custom_element_genres", joinColumns = @JoinColumn(name = "element_id"))
    @Column(name = "genre", nullable = false)
    private List<@NotBlank String> genres;

    private String author;
    private String publisher;
    private LocalDate publicationDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ElementCollection
    @CollectionTable(name = "custom_element_images", joinColumns = @JoinColumn(name = "element_id"))
    @Column(name = "image_url", columnDefinition = "TEXT")
    private List<String> images;

    @ManyToOne(optional = false)
    @JoinColumn(name = "personal_library_id", nullable = false)
    private PersonalLIbrary personalLibrary;
}
