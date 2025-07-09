package com.example.capstone.arkadia.libris.dto.request.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserDto {
    @NotBlank(message = "il nome non può essere vuoto")
    private String name;

    @NotBlank(message = "il cognome non può essere vuoto")
    private String surname;

    @NotBlank(message = "lo username non può essere vuoto")
    private String username;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate bornDate;

    private String phoneNumber;
}
