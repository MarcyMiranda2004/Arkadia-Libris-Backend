package com.example.capstone.arkadia.libris.controller.stock;

import com.example.capstone.arkadia.libris.dto.request.stock.CreateProductRequestDto;
import com.example.capstone.arkadia.libris.dto.request.stock.UpdateProductRequestDto;
import com.example.capstone.arkadia.libris.dto.response.stock.ProductDto;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.exception.ValidationException;
import com.example.capstone.arkadia.libris.service.stock.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired private ProductService productService;

    @GetMapping("/search")
    public ResponseEntity<Page<ProductDto>> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String author,
            @PageableDefault(page = 0, size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(productService.search(title, isbn, author, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ProductDto> create(
            @RequestBody @Valid CreateProductRequestDto dto,
            BindingResult br
    ) {
        if (br.hasErrors()) {
            String errs = br.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .reduce((a, b) -> a + "; " + b).orElse("");
            throw new ValidationException(errs);
        }
        ProductDto created = productService.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ProductDto> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateProductRequestDto dto,
            BindingResult br
    ) throws NotFoundException {
        if (br.hasErrors()) {
            String errs = br.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .reduce((a, b) -> a + "; " + b).orElse("");
            throw new ValidationException(errs);
        }
        return ResponseEntity.ok(productService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws NotFoundException {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
