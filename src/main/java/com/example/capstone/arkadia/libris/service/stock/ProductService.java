package com.example.capstone.arkadia.libris.service.stock;

import com.example.capstone.arkadia.libris.dto.request.stock.CreateProductRequestDto;
import com.example.capstone.arkadia.libris.dto.request.stock.UpdateProductRequestDto;
import com.example.capstone.arkadia.libris.dto.response.stock.ProductDto;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.exception.ValidationException;
import com.example.capstone.arkadia.libris.model.stock.Category;
import com.example.capstone.arkadia.libris.model.stock.Product;
import com.example.capstone.arkadia.libris.repository.stock.CategoryRepository;
import com.example.capstone.arkadia.libris.repository.stock.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private InventoryService inventoryService;

    public Page<ProductDto> search(String title, String isbn, String author, Pageable pageable) {
        return productRepository
                .findByTitleContainingIgnoreCaseOrIsbnContainingIgnoreCaseOrAuthorContainingIgnoreCase(
                        title == null ? "" : title,
                        isbn  == null ? "" : isbn,
                        author== null ? "" : author,
                        pageable
                ).map(this::toDto);
    }

    public ProductDto getById(Long id) throws NotFoundException {
        Product p = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Prodotto non trovato"));
        return toDto(p);
    }

    @Transactional
    public ProductDto create(CreateProductRequestDto req) {
        Product p = new Product();
        p.setTitle(req.getTitle());
        p.setIsbn(req.getIsbn());
        p.setAuthor(req.getAuthor());
        p.setPublisher(req.getPublisher());
        p.setDescription(req.getDescription());
        p.setPrice(req.getPrice());
        p.setProductType(req.getProductType());

        p.getCategories().clear();
        for (String name : req.getCategories()) {
            Category cat = categoryRepository
                    .findByNameIgnoreCaseAndProductCategoryType(name, req.getProductType())
                    .orElseThrow(() -> new ValidationException(
                            "Categoria '" + name + "' non trovata per tipo " + req.getProductType()));
            p.getCategories().add(cat);
        }

        p.getImages().clear();
        p.getImages().addAll(req.getImages());
        if (p.getImages().isEmpty()) {
            p.getImages().add("/images/book-cover-placeholder.png");
        }

        Product saved = productRepository.save(p);
        inventoryService.increase(saved.getId(), req.getInitialStock());
        return toDto(saved);
    }

    @Transactional
    public ProductDto update(Long id, UpdateProductRequestDto req) throws NotFoundException {
        Product p = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Prodotto non trovato"));

        p.setTitle(req.getTitle());
        p.setIsbn(req.getIsbn());
        p.setAuthor(req.getAuthor());
        p.setPublisher(req.getPublisher());
        p.setDescription(req.getDescription());
        p.setPrice(req.getPrice());

        p.getCategories().clear();
        for (String name : req.getCategories()) {
            Category cat = categoryRepository
                    .findByNameIgnoreCaseAndProductCategoryType(name, p.getProductType())
                    .orElseThrow(() -> new ValidationException(
                            "Categoria '" + name + "' non trovata per tipo " + p.getProductType()));
            p.getCategories().add(cat);
        }

        p.getImages().clear();
        p.getImages().addAll(req.getImages());
        if (p.getImages().isEmpty()) {
            p.getImages().add("/images/book-cover-placeholder.png");
        }

        Product updated = productRepository.save(p);
        return toDto(updated);
    }

    @Transactional
    public void delete(Long id) throws NotFoundException {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Prodotto non trovato"));
        productRepository.delete(p);
    }

    private ProductDto toDto(Product p) {
        ProductDto productDto = new ProductDto();
        productDto.setId(p.getId());
        productDto.setTitle(p.getTitle());
        productDto.setIsbn(p.getIsbn());
        productDto.setAuthor(p.getAuthor());
        productDto.setPublisher(p.getPublisher());
        productDto.setDescription(p.getDescription());
        productDto.setPrice(p.getPrice());
        productDto.setCategories(p.getCategories().stream().map(Category::getName).collect(Collectors.toList()));
        productDto.setImages(p.getImages());
        productDto.setStock(inventoryService.getStock(p.getId()));
        return productDto;
    }
}
