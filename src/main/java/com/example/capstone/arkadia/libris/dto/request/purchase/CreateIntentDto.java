package com.example.capstone.arkadia.libris.dto.request.purchase;

import lombok.Data;

@Data
public class CreateIntentDto {
    private long amount;
    private String currency;
    private Long orderId;
}
