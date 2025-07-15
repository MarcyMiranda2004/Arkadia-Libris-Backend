package com.example.capstone.arkadia.libris.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ChangePhoneNumberDto {
    @NotBlank(message = "inserire un numero di telefono")
    @Pattern(regexp = "\\+?[0-9 ]{7,20}", message = "Formato telefono non valido")
    private String phoneNumber;
}
