package com.example.capstone.arkadia.libris.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiError {
    private String message;
    private LocalDateTime errorDate;
}
