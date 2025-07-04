package com.example.capstone.arkadia.libris.model.stock;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "inventory_items")
public class InventoryItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;
}
