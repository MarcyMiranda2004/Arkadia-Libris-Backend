package com.example.capstone.arkadia.libris.service.stock;

import com.example.capstone.arkadia.libris.dto.request.stock.CreateAndUpdateCategoryRequestDto;
import com.example.capstone.arkadia.libris.dto.response.stock.CategoryDto;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.model.stock.Category;
import com.example.capstone.arkadia.libris.repository.stock.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<CategoryDto> listAll() {
        return categoryRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getById(Long id) throws NotFoundException {
        Category c = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria non trovata"));
        return toDto(c);
    }

    @Transactional
    public CategoryDto create(CreateAndUpdateCategoryRequestDto request) {
        return categoryRepository
                .findByNameIgnoreCaseAndProductCategoryType(request.getName(), request.getProductCategoryType())
                .map(this::toDto)
                .orElseGet(() -> {
                    Category c = new Category();
                    c.setName(request.getName());
                    c.setProductCategoryType(request.getProductCategoryType());
                    return toDto(categoryRepository.save(c));
                });
    }

    @Transactional
    public CategoryDto update(Long id, CreateAndUpdateCategoryRequestDto request) throws NotFoundException {
        Category c = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria non trovata"));
        c.setName(request.getName());
        c.setProductCategoryType(request.getProductCategoryType());
        return toDto(categoryRepository.save(c));
    }

    @Transactional
    public void delete(Long id) throws NotFoundException {
        Category c = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria non trovata"));
        categoryRepository.delete(c);
    }

    private CategoryDto toDto(Category c) {
        CategoryDto dto = new CategoryDto();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setProductCategoryType(c.getProductCategoryType());
        return dto;
    }
}
