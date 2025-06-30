package com.example.capstone.arkadia.libris.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginDto {

    @NotEmpty(message = "inserire un'email ")
    private String email;
    @NotEmpty(message = "inserire la password")
    private String password;
}
