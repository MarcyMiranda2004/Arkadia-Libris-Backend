package com.example.capstone.arkadia.libris.security.forgot;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordDto {
    @NotBlank
    @Email
    private String email;
}

