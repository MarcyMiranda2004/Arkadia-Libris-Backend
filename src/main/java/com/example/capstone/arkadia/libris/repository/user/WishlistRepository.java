package com.example.capstone.arkadia.libris.repository.user;

import com.example.capstone.arkadia.libris.model.user.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByUserId(Long userId);
}
