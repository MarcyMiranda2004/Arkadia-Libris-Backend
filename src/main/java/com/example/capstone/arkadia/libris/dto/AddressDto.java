package com.example.capstone.arkadia.libris.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressDto {
    private Long id;

    @NotBlank(message = "Dai un nome all'indirizzo")
    private String name;

    @NotBlank(message = "Inserire la Via")
    private String street;

    @NotBlank(message = "Inserire la città")
    private String city;

    @NotBlank(message = "Inserire il codice postale")
    private String Country;

    @NotBlank(message = "Inserire il codice postale")
    private String postalCode;
}
