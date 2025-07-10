package com.example.capstone.arkadia.libris.controller.purchase;

import com.example.capstone.arkadia.libris.dto.request.user.CheckoutRequestDto;
import com.example.capstone.arkadia.libris.dto.response.purchase.OrderDto;
import com.example.capstone.arkadia.libris.enumerated.OrderStatus;
import com.example.capstone.arkadia.libris.exception.NotFoundException;
import com.example.capstone.arkadia.libris.service.purchase.OrderService;
import jakarta.validation.Valid;
import jdk.jshell.Snippet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}/orders")
public class OrderController {

    @Autowired private OrderService orderService;

    @GetMapping
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Page<OrderDto>> viewOrders(
            @PathVariable("userId") Long userId,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) throws NotFoundException {
        return ResponseEntity.ok(orderService.listOrders(userId, pageable));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<OrderDto> viewSingleOrder(
            @PathVariable("userId") Long userId,
            @PathVariable("orderId") Long orderId
    ) throws NotFoundException {
        return ResponseEntity.ok(orderService.getOrder(userId, orderId));
    }

    @PostMapping("/checkout")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<OrderDto> checkout(
            @PathVariable Long userId,
            @RequestBody @Valid CheckoutRequestDto request
    ) throws NotFoundException {
        OrderDto dto = orderService.checkout(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PatchMapping("{orderId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long userId,
            @PathVariable Long orderId,
            @RequestParam OrderStatus status
    ) throws NotFoundException {
        orderService.updateStatus(userId, orderId, status);
        return ResponseEntity.noContent().build();
    }
}
