package com.example.capstone.arkadia.libris.model.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference
    private List<PersonalLibraryItem> items = new ArrayList<>();
}
