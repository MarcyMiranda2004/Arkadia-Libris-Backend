package com.example.capstone.arkadia.libris.dto.response.purchase;

import lombok.Data;

@Data
public class CreateIntentResponse {
    private Long orderId;
    private String clientSecret;
}

