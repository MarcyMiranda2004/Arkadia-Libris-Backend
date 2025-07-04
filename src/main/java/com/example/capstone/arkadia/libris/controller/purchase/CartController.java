package com.example.capstone.arkadia.libris.controller.purchase;

import com.example.capstone.arkadia.libris.dto.request.purchase.AddToCartRequestDto;
import com.example.capstone.arkadia.libris.dto.response.purchase.CartDto;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.service.purchase.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/users/{userId}/cart")
@Validated
public class CartController {

    @Autowired private CartService cartService;

    @GetMapping
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<CartDto> viewCart(@PathVariable("userId") Long userId) throws NotFoundException {
        CartDto cartDto = cartService.viewCart(userId);
        return ResponseEntity.ok(cartDto);
    }

    @PostMapping
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<CartDto> addItem(
            @PathVariable("userId") Long userId,
            @RequestBody @Valid AddToCartRequestDto request
    ) throws NotFoundException {
        CartDto cartDto = cartService.addItemToCart(userId, request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(cartDto);
    }

    @PatchMapping(path = "/{productId}")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<CartDto> updateItemQuantity(
            @PathVariable("userId") Long userId,
            @PathVariable("productId") Long productId,
            @RequestParam @Min(value = 0, message = "La quantità non può essere negativa") int quantity
    ) throws NotFoundException {
        CartDto cartDto = cartService.updateItemQuantity(userId, productId, quantity);
        return ResponseEntity.ok(cartDto);
    }

    @DeleteMapping(path = "/{productId}")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<CartDto> removeItem(
            @PathVariable("userId") Long userId,
            @PathVariable("productId") Long productId
    ) throws NotFoundException {
        CartDto cartDto = cartService.removeItemFromCart(userId, productId);
        return ResponseEntity.ok(cartDto);
    }

    @DeleteMapping
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<Void> clearCart(
            @PathVariable("userId") Long userId
    ) throws NotFoundException {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
