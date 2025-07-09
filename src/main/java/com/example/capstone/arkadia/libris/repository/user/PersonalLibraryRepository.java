package com.example.capstone.arkadia.libris.repository.user;

import com.example.capstone.arkadia.libris.model.user.PersonalLIbrary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonalLibraryRepository extends JpaRepository<PersonalLIbrary, Long> {
    Optional<PersonalLIbrary> findByUserId(Long userId);
}
