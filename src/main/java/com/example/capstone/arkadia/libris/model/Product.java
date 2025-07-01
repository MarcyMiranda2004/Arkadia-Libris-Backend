package com.example.capstone.arkadia.libris.model;

import com.example.capstone.arkadia.libris.enumerated.ProductType;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "prodotti")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    private String description;
    private LocalDate publicationDate;
    private String coverURL;
    private String isbn;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_prodotto", nullable = false)
    private ProductType tipoProdotto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "genere_id")
    private Category category;
}
