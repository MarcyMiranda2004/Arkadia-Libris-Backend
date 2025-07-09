package com.example.capstone.arkadia.libris.repository.user;

import com.example.capstone.arkadia.libris.model.user.PersonalLibraryCustomItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PersonalLibraryCustomItemRepository extends JpaRepository<PersonalLibraryCustomItem, Long> {
    List<PersonalLibraryCustomItem> findByPersonalLibraryId(Long libraryId);
    Optional<PersonalLibraryCustomItem> findByIdAndPersonalLibraryId(Long id, Long libraryId);
}
