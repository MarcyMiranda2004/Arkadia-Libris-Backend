package com.example.capstone.arkadia.libris.model.user;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "personal_library")
public class PersonalLIbrary {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "personalLibrary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonalLibraryItem> items = new ArrayList<>();
}
