package com.example.capstone.arkadia.libris.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteUserDto {
    @NotBlank(message = "Devi inserire la password per confermare")
    private String password;
}
