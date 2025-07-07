package com.example.capstone.arkadia.libris.service.stock;

import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.exception.OutOfStockException;
import com.example.capstone.arkadia.libris.model.stock.InventoryItem;
import com.example.capstone.arkadia.libris.model.stock.Product;
import com.example.capstone.arkadia.libris.repository.stock.InventoryRepository;
import com.example.capstone.arkadia.libris.repository.stock.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    @Autowired private InventoryRepository inventoryRepository;
    @Autowired private ProductRepository productRepository;

    @Transactional
    public void increase(Long productId, int quantity) {
        InventoryItem inv = inventoryRepository.findByProductId(productId)
                .orElseGet(() -> {
                    InventoryItem i = new InventoryItem();
                    Product p = productRepository.getReferenceById(productId);
                    i.setProduct(p);
                    i.setQuantity(0);
                    return i;
                });
        inv.setQuantity(inv.getQuantity() + quantity);
        inventoryRepository.save(inv);
    }

    @Transactional
    public void decrease(Long productId, int quantity) {
        InventoryItem inv = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException(productId + " non trovato nell'inventario"));
        if (inv.getQuantity() < quantity) {
            throw new OutOfStockException("Quantità insufficiente (disponibili: " + inv.getQuantity() + ")");
        }
        inv.setQuantity(inv.getQuantity() - quantity);
        inventoryRepository.save(inv);
    }

    public int getStock(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .map(InventoryItem::getQuantity)
                .orElse(0);
    }

    public List<InventoryItem> listAll() {
        return inventoryRepository.findAll();
    }
}
