// src/main/java/com/example/capstone/arkadia/libris/model/stock/Product.java
package com.example.capstone.arkadia.libris.model.stock;

import com.example.capstone.arkadia.libris.enumerated.ProductType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String publisher;
    private String description;
    private LocalDate publicationDate;
    private String isbn;
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false)
    private ProductType productType;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "product_categories", joinColumns =
    @JoinColumn(name = "product_id"), inverseJoinColumns =
    @JoinColumn(name = "category_id"))
    private List<Category> categories = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private List<String> images = new ArrayList<>();
}
