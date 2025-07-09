package com.example.capstone.arkadia.libris.dto.response.purchase;

import com.example.capstone.arkadia.libris.dto.response.user.AddressDto;
import com.example.capstone.arkadia.libris.enumerated.OrderStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class OrderDto {
    private Long orderId;
    private LocalDate orderDate;
    private double totalAmmount;
    private AddressDto shippingAddress;
    private AddressDto billingAddress;
    private List<OrderItemDto> items;
    private OrderStatus orderStatus;
}
