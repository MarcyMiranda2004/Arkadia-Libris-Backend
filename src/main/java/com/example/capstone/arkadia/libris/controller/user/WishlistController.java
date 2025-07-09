package com.example.capstone.arkadia.libris.controller.user;

import com.example.capstone.arkadia.libris.dto.request.user.AddToWishlistRequestDto;
import com.example.capstone.arkadia.libris.dto.response.user.WishlistDto;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.service.user.WishlistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<WishlistDto> viewWishlist(@PathVariable Long userId) throws NotFoundException {
        return ResponseEntity.ok(wishlistService.viewWishlist(userId));
    }

    @PostMapping
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<WishlistDto> addItemToWishlist(
            @PathVariable Long userId,
            @RequestBody @Valid AddToWishlistRequestDto request
    ) throws NotFoundException {
        return ResponseEntity.ok(
                wishlistService.addItemToWishlist(userId, request.getProductId())
        );
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<WishlistDto> removeItemFromWishlist(
            @PathVariable Long userId,
            @PathVariable Long productId
    ) throws NotFoundException {
        return ResponseEntity.ok(wishlistService.removeItemFromWishlist(userId, productId));
    }

    @DeleteMapping
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<Void> clearCart(
            @PathVariable Long userId
    ) throws NotFoundException {
        wishlistService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
