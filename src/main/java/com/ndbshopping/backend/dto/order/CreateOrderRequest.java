package com.ndbshopping.backend.dto.order;

import com.ndbshopping.backend.entity.enums.DeliveryCity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotNull DeliveryCity villeLivraison,
        @NotBlank String adresseDetails
) {
}
