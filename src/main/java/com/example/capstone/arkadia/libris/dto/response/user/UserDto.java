package com.example.capstone.arkadia.libris.dto.response.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {
    @NotBlank(message = "il nome non può essere vuoto")
    private String name;

    @NotBlank(message = "il cognome non può essere vuoto")
    private String surname;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate bornDate;

    private String phoneNumber;

    @NotBlank(message = "lo username non può essere vuoto")
    private String username;

    @NotBlank(message = "l'email non può essere vuota")
    @Email(message = "formato email non valido")
    private String email;

    @NotBlank(message = "la password non può essere vuota")
    @Size(min = 8, message = "la password deve essere di almeno 8 caratteri")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$£%^&+*=_!?~/|.,:;§]).*$",
            message = "La password deve contenere almeno una lettera maiuscola, una minuscola, un numero e un carattere speciale (@ # $ £ % ^ & + * = _ ! ? ~ / | . , : ; §)"
    )
    private String password;

    private String avatarUrl;
}
