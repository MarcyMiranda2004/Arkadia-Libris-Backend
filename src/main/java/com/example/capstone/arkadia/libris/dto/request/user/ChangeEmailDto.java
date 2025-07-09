package com.example.capstone.arkadia.libris.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeEmailDto {
    @NotBlank(message = "inserisci la password per cambiare l'email")
    private String password;

    @NotBlank(message = "devi inserire l'email corrente")
    @Email(message = "Formato email corrente non valido")
    private String currentEmail;

    @NotBlank(message = "devi inserire la nuova email")
    @Email(message = "Formato nuova email non valido")
    private String newEmail;

    @NotBlank(message = "La conferma della password non può essere vuota")
    @Email(message = "Formato conferma email non valido")
    private String confirmNewEmail;
}
