package com.example.capstone.arkadia.libris.repository.purchase;

import com.example.capstone.arkadia.libris.model.purchase.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
}
