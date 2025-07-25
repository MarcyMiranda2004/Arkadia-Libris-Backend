package com.example.capstone.arkadia.libris.service.stock;

import com.example.capstone.arkadia.libris.dto.request.stock.CreateProductRequestDto;
import com.example.capstone.arkadia.libris.dto.request.stock.UpdateProductRequestDto;
import com.example.capstone.arkadia.libris.dto.response.stock.ProductDto;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.exception.ValidationException;
import com.example.capstone.arkadia.libris.model.stock.Category;
import com.example.capstone.arkadia.libris.model.stock.Product;
import com.example.capstone.arkadia.libris.repository.stock.CategoryRepository;
import com.example.capstone.arkadia.libris.repository.stock.InventoryRepository;
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
    @Autowired private InventoryRepository inventoryRepository;
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

    public Product getProductById(Long id) throws NotFoundException {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException("Prodotto non trovato"));
    }

    @Transactional
    public ProductDto create(CreateProductRequestDto request) {
        Product p = new Product();
        p.setTitle(request.getTitle());
        p.setIsbn(request.getIsbn());
        p.setAuthor(request.getAuthor());
        p.setPublisher(request.getPublisher());
        p.setDescription(request.getDescription());
        p.setPrice(request.getPrice());
        p.setProductType(request.getProductType());

        p.getCategories().clear();
        for (String name : request.getCategories()) {
            Category cat = categoryRepository
                    .findByNameIgnoreCaseAndProductCategoryType(name, request.getProductType())
                    .orElseThrow(() -> new ValidationException(
                            "Categoria '" + name + "' non trovata per tipo " + request.getProductType()));
            p.getCategories().add(cat);
        }

        p.getImages().clear();
        p.getImages().addAll(request.getImages());
        if (p.getImages().isEmpty()) {
            p.getImages().add("https://i.pinimg.com/736x/5d/9e/c5/5d9ec5890c8e5cf8185e4bc96e9fc015.jpg");
        }

        Product saved = productRepository.save(p);
        inventoryService.increase(saved.getId(), request.getInitialStock());
        return toDto(saved);
    }

    @Transactional
    public ProductDto update(Long id, UpdateProductRequestDto request) throws NotFoundException {
        Product p = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Prodotto non trovato"));

        p.setTitle(request.getTitle());
        p.setIsbn(request.getIsbn());
        p.setAuthor(request.getAuthor());
        p.setPublisher(request.getPublisher());
        p.setDescription(request.getDescription());
        p.setPrice(request.getPrice());

        p.getCategories().clear();
        for (String name : request.getCategories()) {
            Category cat = categoryRepository
                    .findByNameIgnoreCaseAndProductCategoryType(name, p.getProductType())
                    .orElseThrow(() -> new ValidationException(
                            "Categoria '" + name + "' non trovata per tipo " + p.getProductType()));
            p.getCategories().add(cat);
        }

        p.getImages().clear();
        p.getImages().addAll(request.getImages());
        if (p.getImages().isEmpty()) {
            p.getImages().add("/images/book-cover-placeholder.png");
        }

        Product updated = productRepository.save(p);
        return toDto(updated);
    }

    @Transactional
    public void delete(Long id) throws NotFoundException {
        inventoryRepository.findByProductId(id)
                .ifPresent(inventoryRepository::delete);

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
