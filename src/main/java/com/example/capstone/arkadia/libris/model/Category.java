package com.example.capstone.arkadia.libris.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "generi")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
