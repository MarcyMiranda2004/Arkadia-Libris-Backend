package com.example.capstone.arkadia.libris.dto.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckoutRequestDto {
    @NotNull(message="Devi specificare l'indirizzo di spedizione")
    private Long shippingAddressId;

    private Long billingAddressId;
}
