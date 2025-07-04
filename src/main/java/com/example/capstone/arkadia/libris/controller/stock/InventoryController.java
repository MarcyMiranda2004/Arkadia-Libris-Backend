// src/main/java/com/example/capstone/arkadia/libris/controller/stock/InventoryController.java
package com.example.capstone.arkadia.libris.controller.stock;

import com.example.capstone.arkadia.libris.dto.request.stock.StockRequestDto;
import com.example.capstone.arkadia.libris.dto.response.stock.InventoryItemDto;
import com.example.capstone.arkadia.libris.model.stock.InventoryItem;
import com.example.capstone.arkadia.libris.service.stock.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products/{productId}/stock")
public class InventoryController {

    @Autowired private InventoryService inventoryService;

    @PostMapping("/increase")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Void> increase(
            @PathVariable Long productId,
            @RequestBody @Valid StockRequestDto dto
    ) {
        inventoryService.increase(productId, dto.getQuantity());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/decrease")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Void> decrease(
            @PathVariable Long productId,
            @RequestBody @Valid StockRequestDto dto
    ) {
        inventoryService.decrease(productId, dto.getQuantity());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<InventoryItemDto>> listAll(
            @PathVariable Long productId
    ) {
        List<InventoryItemDto> dto = inventoryService.listAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dto);
    }

    private InventoryItemDto toDto(InventoryItem inv) {
        InventoryItemDto d = new InventoryItemDto();
        d.setProductId(inv.getProduct().getId());
        d.setTitle(inv.getProduct().getTitle());
        d.setQuantity(inv.getQuantity());
        return d;
    }
}
