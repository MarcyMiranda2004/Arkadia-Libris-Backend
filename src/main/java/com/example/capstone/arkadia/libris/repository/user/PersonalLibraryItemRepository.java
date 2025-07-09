package com.example.capstone.arkadia.libris.repository.user;

import com.example.capstone.arkadia.libris.model.user.PersonalLibraryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonalLibraryItemRepository extends JpaRepository<PersonalLibraryItem, Long> {
    Optional<PersonalLibraryItem> findByPersonalLibraryIdAndProductId(Long libId, Long productId);
    void deleteByPersonalLibraryIdAndProductId(Long libId, Long productId);
}
