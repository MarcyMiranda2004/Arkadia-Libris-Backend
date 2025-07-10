package com.example.capstone.arkadia.libris.dto.request.administration;

import com.example.capstone.arkadia.libris.enumerated.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateStaffRequestDto {
    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    private LocalDate bornDate;

    @NotBlank
    private String username;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private Role role;
}