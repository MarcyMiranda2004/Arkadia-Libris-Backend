package com.example.capstone.arkadia.libris.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginDto {
    @Email
    @NotBlank(message = "inserire un'email ")
    private String email;

    @NotBlank(message = "inserire la password")
    private String password;

}
