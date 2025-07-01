package com.example.capstone.arkadia.libris.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDto {
    @NotBlank(message = "La password corrente non può essere vuota")
    private String oldPassword;

    @NotBlank(message = "La nuova password non può essere vuota")
    @Size(min = 8, message = "La nuova password deve essere di almeno 8 caratteri")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$£%^&+*=_!?~/|.,:;§]).*$",
            message = "La password deve contenere almeno una lettera maiuscola, una minuscola, un numero e un carattere speciale (@ # $ £ % ^ & + * = _ ! ? ~ / | . , : ; §)"
    )
    private String newPassword;

    @NotBlank(message = "La conferma della password non può essere vuota")
    private String confirmNewPassword;
}
