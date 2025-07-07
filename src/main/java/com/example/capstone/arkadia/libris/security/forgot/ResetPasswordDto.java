package com.example.capstone.arkadia.libris.security.forgot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordDto {
    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 8, message = "la password deve essere di almeno 8 caratteri")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$£%^&+*=_!?~/|.,:;§]).*$",
            message = "La password deve contenere almeno una lettera maiuscola, una minuscola, un numero e un carattere speciale (@ # $ £ % ^ & + * = _ ! ? ~ / | . , : ; §)"
    )
    private String newPassword;
}
