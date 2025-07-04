package com.example.capstone.arkadia.libris.service.purchase;

import com.example.capstone.arkadia.libris.dto.request.user.CheckoutRequestDto;
import com.example.capstone.arkadia.libris.dto.response.user.AddressDto;
import com.example.capstone.arkadia.libris.dto.response.purchase.OrderDto;
import com.example.capstone.arkadia.libris.dto.response.purchase.OrderItemDto;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.exception.OutOfStockException;
import com.example.capstone.arkadia.libris.model.user.Address;
import com.example.capstone.arkadia.libris.model.purchase.CartItem;
import com.example.capstone.arkadia.libris.model.purchase.Order;
import com.example.capstone.arkadia.libris.model.purchase.OrderItem;
import com.example.capstone.arkadia.libris.model.user.User;
import com.example.capstone.arkadia.libris.repository.user.AddressRepository;
import com.example.capstone.arkadia.libris.repository.purchase.OrderRepository;
import com.example.capstone.arkadia.libris.service.stock.InventoryService;
import com.example.capstone.arkadia.libris.service.notification.EmailService;
import com.example.capstone.arkadia.libris.service.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired private InventoryService inventoryService;
    @Autowired private CartService cartService;
    @Autowired private UserService userService;
    @Autowired private AddressRepository addressRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private EmailService emailService;

    @Transactional
    public OrderDto checkout(long userId, CheckoutRequestDto request) throws NotFoundException {
        User u = userService.getUser(userId);
        List<CartItem> cartItems = cartService.getCartItems(userId);
        if (cartItems.isEmpty()) throw new NotFoundException("Il carrello è vuoto");

        Address ship = addressRepository.findById(request.getShippingAddressId())
                .filter(a -> a.getUser().getId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Shipping address non valido"));

        Address bill = request.getBillingAddressId() != null
                ? addressRepository.findById(request.getBillingAddressId())
                .filter(a -> a.getUser().getId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Billing address non valido"))
                : ship;

        Order order = new Order();
        order.setUser(u);
        order.setOrderDate(LocalDate.now());
        order.setShippingAddress(ship);
        order.setBillingAddress(bill);

        List<OrderItem> orderItems = cartItems.stream().map(ci -> {
            long pid = ci.getProduct().getId();
            int qty = ci.getQuantity();
            int stock = inventoryService.getStock(pid);
            if (stock == 0) throw new OutOfStockException("Prodotto " + pid + " esaurito");
            if (stock < qty) throw new OutOfStockException("Quantità insufficiente per prodotto " + pid);
            inventoryService.decrease(pid, qty);
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(ci.getProduct());
            oi.setQuantity(qty);
            oi.setUnitPrice(ci.getProduct().getPrice());
            return oi;
        }).collect(Collectors.toList());
        order.setItems(orderItems);

        double total = orderItems.stream().mapToDouble(i -> i.getQuantity() * i.getUnitPrice()).sum();
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        cartService.clearCart(userId);
        emailService.sendOrderConfirmation(u, saved.getId(), total);

        return toDto(saved);
    }

    public Page<OrderDto> listOrders(long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable).map(this::toDto);
    }

    public OrderDto getOrder(long userId, long orderId) throws NotFoundException {
        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getUser().getId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Ordine non trovato"));
        return toDto(order);
    }

    private OrderDto toDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setOrderId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmmount(order.getTotalAmount());
        dto.setShippingAddress(mapAddress(order.getShippingAddress()));
        dto.setBillingAddress(mapAddress(order.getBillingAddress()));
        List<OrderItemDto> items = order.getItems().stream().map(oi -> {
            OrderItemDto it = new OrderItemDto();
            it.setProductId(oi.getProduct().getId());
            it.setProductName(oi.getProduct().getTitle());
            it.setQuantity(oi.getQuantity());
            it.setPrice(oi.getUnitPrice());
            return it;
        }).collect(Collectors.toList());
        dto.setItems(items);
        return dto;
    }

    private AddressDto mapAddress(Address a) {
        AddressDto ad = new AddressDto();
        ad.setId(a.getId());
        ad.setName(a.getName());
        ad.setStreet(a.getStreet());
        ad.setCity(a.getCity());
        ad.setCountry(a.getCountry());
        ad.setPostalCode(a.getPostalCode());
        return ad;
    }
}
