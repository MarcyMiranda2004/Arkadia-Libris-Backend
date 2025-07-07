package com.example.capstone.arkadia.libris.model.user;

import com.example.capstone.arkadia.libris.model.stock.Product;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "wishlist_items")
public class WishlistItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
