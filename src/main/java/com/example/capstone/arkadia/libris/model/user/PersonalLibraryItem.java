package com.example.capstone.arkadia.libris.model.user;

import com.example.capstone.arkadia.libris.enumerated.ReadingStatus;
import com.example.capstone.arkadia.libris.model.stock.Product;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "personal_library_items")
public class PersonalLibraryItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "personal_library_id", nullable = false)
    private PersonalLIbrary personalLibrary;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingStatus status = ReadingStatus.DA_LEGGERE;
}
