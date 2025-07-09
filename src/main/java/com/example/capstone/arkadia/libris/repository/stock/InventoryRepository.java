package com.example.capstone.arkadia.libris.repository.stock;

import com.example.capstone.arkadia.libris.model.stock.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    Optional<InventoryItem> findByProductId(Long productId);
}
