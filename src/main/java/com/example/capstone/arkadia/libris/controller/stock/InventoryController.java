package com.example.capstone.arkadia.libris.controller.stock;

import com.example.capstone.arkadia.libris.dto.request.stock.StockRequestDto;
import com.example.capstone.arkadia.libris.dto.response.stock.InventoryItemDto;
import com.example.capstone.arkadia.libris.dto.response.stock.ProductDto;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.service.stock.InventoryService;
import com.example.capstone.arkadia.libris.service.stock.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class InventoryController {

    @Autowired private InventoryService inventoryService;
    @Autowired private ProductService    productService;

    @PostMapping("/{productId}/stock/increase")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Void> increase(
            @PathVariable Long productId,
            @RequestBody @Valid StockRequestDto stockRequestDto
    ) {
        inventoryService.increase(productId, stockRequestDto.getQuantity());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{productId}/stock/decrease")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Void> decrease(
            @PathVariable Long productId,
            @RequestBody @Valid StockRequestDto stockRequestDto
    ) {
        inventoryService.decrease(productId, stockRequestDto.getQuantity());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{productId}/stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<InventoryItemDto> getStock(
            @PathVariable Long productId
    ) throws NotFoundException {
        ProductDto productDto = productService.getById(productId);
        InventoryItemDto inventoryItemDto = new InventoryItemDto();
        inventoryItemDto.setProductId(productId);
        inventoryItemDto.setTitle(productDto.getTitle());
        inventoryItemDto.setQuantity(inventoryService.getStock(productId));
        return ResponseEntity.ok(inventoryItemDto);
    }

    @GetMapping("/stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<InventoryItemDto>> listAll() {
        List<InventoryItemDto> list = inventoryService.listAll().stream()
                .map(inv -> {
                    InventoryItemDto d = new InventoryItemDto();
                    d.setProductId(inv.getProduct().getId());
                    d.setTitle(inv.getProduct().getTitle());
                    d.setQuantity(inv.getQuantity());
                    return d;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
}
