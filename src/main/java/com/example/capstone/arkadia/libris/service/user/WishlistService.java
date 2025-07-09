package com.example.capstone.arkadia.libris.service.user;

import com.example.capstone.arkadia.libris.dto.response.user.WishlistDto;
import com.example.capstone.arkadia.libris.dto.response.user.WishlistItemDto;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.model.stock.Product;
import com.example.capstone.arkadia.libris.model.user.User;
import com.example.capstone.arkadia.libris.model.user.Wishlist;
import com.example.capstone.arkadia.libris.model.user.WishlistItem;
import com.example.capstone.arkadia.libris.repository.stock.ProductRepository;
import com.example.capstone.arkadia.libris.repository.user.WishlistRepository;
import com.example.capstone.arkadia.libris.service.stock.InventoryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WishlistService {

    @Autowired private UserService userService;
    @Autowired private WishlistRepository wishlistRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private InventoryService inventoryService;

    public WishlistDto viewWishlist(Long userId) throws NotFoundException {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Wishlist non trovata per utente " + userId));

        WishlistDto wishlistDto = new WishlistDto();
        wishlistDto.setWishlistId(wishlist.getId());
        wishlistDto.setItems(wishlist.getItems().stream().map(this::toDto).toList());
        return  wishlistDto;
    }

    public List<WishlistItem> getWishlistItems(Long userId) throws NotFoundException {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Wishlist non trovata per utente " + userId));
        return wishlist.getItems();
    }

    @Transactional
    public WishlistDto addItemToWishlist(Long userId, Long productId) throws NotFoundException {
       User u = userService.getUser(userId);
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Prodotto non trovato: " + productId));

        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wishlist w = new Wishlist();
                    w.setUser(u);
                    return w;
                });

        Optional<WishlistItem> existing = wishlist.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId)).findAny();

        WishlistItem item = existing.orElseGet(() -> {
            WishlistItem wi = new WishlistItem();
            wi.setWishlist(wishlist);
            wi.setProduct(p);
            wishlist.getItems().add(wi);
            return wi;
        });

        wishlistRepository.save(wishlist);
        return viewWishlist(userId);
    }

    @Transactional
    public WishlistDto removeItemFromWishlist(Long userId, Long productId) throws NotFoundException {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Wishlist non trovata per utente " + userId));
        wishlist.getItems().removeIf(i -> i.getProduct().getId().equals(productId));
        wishlistRepository.save(wishlist);
        return viewWishlist(userId);
    }

    @Transactional
    public void clearCart(Long userId) throws NotFoundException {
        wishlistRepository.findByUserId(userId).ifPresent(w -> {
            w.getItems().clear();
            wishlistRepository.save(w);
        });
    }

    private WishlistItemDto toDto(WishlistItem i) {
        WishlistItemDto wi = new WishlistItemDto();
        wi.setProductId(i.getProduct().getId());
        wi.setProductName(i.getProduct().getTitle());
        return wi;
    }
}
