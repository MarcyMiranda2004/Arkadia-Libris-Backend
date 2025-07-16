package com.example.capstone.arkadia.libris.service.purchase;

import com.example.capstone.arkadia.libris.dto.response.purchase.CartDto;
import com.example.capstone.arkadia.libris.dto.response.purchase.CartItemDto;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.exception.OutOfStockException;
import com.example.capstone.arkadia.libris.model.purchase.Cart;
import com.example.capstone.arkadia.libris.model.purchase.CartItem;
import com.example.capstone.arkadia.libris.model.stock.Product;
import com.example.capstone.arkadia.libris.model.user.User;
import com.example.capstone.arkadia.libris.repository.purchase.CartRepository;
import com.example.capstone.arkadia.libris.repository.stock.ProductRepository;
import com.example.capstone.arkadia.libris.service.stock.InventoryService;
import com.example.capstone.arkadia.libris.service.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired private UserService userService;
    @Autowired private CartRepository cartRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private InventoryService inventoryService;

    public CartDto viewCart(Long userId) throws NotFoundException {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Carrello non trovato per utente " + userId));

        var items = cart.getItems().stream().map(this::toDto).collect(Collectors.toList());

        double total = items.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();

        CartDto dto = new CartDto();
        dto.setCartId(cart.getId());
        dto.setItems(items);
        dto.setTotalPrice(total);
        return dto;
    }

    public List<CartItem> getCartItems(Long userId) throws NotFoundException {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Carrello non trovato per utente " + userId));
        return cart.getItems();
    }

    @Transactional
    public CartDto addItemToCart(Long userId, Long productId, int quantity) throws NotFoundException {
        User u = userService.getUser(userId);
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Prodotto non trovato: " + productId));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUser(u);
                    return cartRepository.save(c);
                });

        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findAny();

        CartItem item = existing.orElseGet(() -> {
            CartItem ci = new CartItem();
            ci.setCart(cart);
            ci.setProduct(p);
            ci.setQuantity(0);
            cart.getItems().add(ci);
            return ci;
        });

        int available = inventoryService.getStock(productId);
        if (available < item.getQuantity() + quantity) {
            throw new OutOfStockException(
                    "Il prodotto \"" + p.getTitle() + "\" ha solo " + available + " pezzi disponibili");
        }

        item.setQuantity(item.getQuantity() + quantity);
        cartRepository.save(cart);
        return viewCart(userId);
    }

    @Transactional
    public CartDto updateItemQuantity(Long userId, Long productId, int quantity) throws NotFoundException {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Carrello non trovato per utente " + userId));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Prodotto non trovato nel carrello: " + productId));

        if (quantity < 0) {
            throw new IllegalArgumentException("La quantità non può essere negativa");
        }

        int available = inventoryService.getStock(productId);
        if (available < quantity) {
            throw new OutOfStockException(
                    "Il prodotto \"" + item.getProduct().getTitle() + "\" ha solo " + available + " pezzi disponibili");
        }

        item.setQuantity(quantity);
        cartRepository.save(cart);
        return viewCart(userId);
    }

    @Transactional
    public CartDto removeItemFromCart(Long userId, Long productId) throws NotFoundException {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Carrello non trovato per utente " + userId));

        cart.getItems().removeIf(i -> i.getProduct().getId().equals(productId));
        cartRepository.save(cart);
        return viewCart(userId);
    }

    @Transactional
    public void clearCart(Long userId) throws NotFoundException {
        cartRepository.findByUserId(userId).ifPresent(c -> {
            c.getItems().clear();
            cartRepository.save(c);
        });
    }

    private CartItemDto toDto(CartItem i) {
        CartItemDto d = new CartItemDto();
        d.setProductId(i.getProduct().getId());
        d.setProductName(i.getProduct().getTitle());
        d.setImageUrls(i.getProduct().getImages());
        d.setQuantity(i.getQuantity());
        d.setPrice(i.getProduct().getPrice());
        return d;
    }
}
