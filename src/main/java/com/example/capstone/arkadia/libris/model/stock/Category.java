package com.example.capstone.arkadia.libris.model.stock;

import com.example.capstone.arkadia.libris.enumerated.ProductType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "categories", uniqueConstraints =
@UniqueConstraint(name = "uc_genere_nome_tipo", columnNames = {"name", "product_category_type"}))
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_category_type", nullable = false)
    private ProductType productCategoryType;
}

