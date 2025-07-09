package com.example.capstone.arkadia.libris.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "personal_library_custom_items")
public class PersonalLibraryCustomItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "personal_library_id", nullable = false)
    private PersonalLIbrary personalLibrary;

    @NotBlank(message = "devi dare un titolo al tuo elemento")
    private String title;

    @NotBlank(message = "Devi specificare il tipo di elemento")
    private String category;

    @ElementCollection
    @CollectionTable(name = "personal_library_custom_genres", joinColumns = @JoinColumn(name = "item_id"))
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
    @CollectionTable(name = "personal_library_custom_images", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "image_url", columnDefinition = "TEXT")
    private List<String> imageUrls;
}
