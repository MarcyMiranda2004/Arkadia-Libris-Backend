package com.example.capstone.arkadia.libris.controller.stock;

import com.example.capstone.arkadia.libris.dto.request.stock.CreateAndUpdateCategoryRequestDto;
import com.example.capstone.arkadia.libris.dto.response.stock.CategoryDto;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.exception.ValidationException;
import com.example.capstone.arkadia.libris.service.stock.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> listAll() {
        return ResponseEntity.ok(categoryService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getById(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<CategoryDto> create(
            @RequestBody @Valid CreateAndUpdateCategoryRequestDto request,
            BindingResult br
    ) {
        if (br.hasErrors()) {
            String errs = br.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("");
            throw new ValidationException(errs);
        }
        CategoryDto created = categoryService.create(request);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<CategoryDto> update(
            @PathVariable Long id,
            @RequestBody @Valid CreateAndUpdateCategoryRequestDto request,
            BindingResult br
    ) throws NotFoundException {
        if (br.hasErrors()) {
            String errs = br.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("");
            throw new ValidationException(errs);
        }
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws NotFoundException {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
