package com.example.capstone.arkadia.libris.repository.stock;

import com.example.capstone.arkadia.libris.enumerated.ProductType;
import com.example.capstone.arkadia.libris.model.stock.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameIgnoreCase(String name);
    Optional<Category> findByNameIgnoreCaseAndProductCategoryType(String name, ProductType type);
}
