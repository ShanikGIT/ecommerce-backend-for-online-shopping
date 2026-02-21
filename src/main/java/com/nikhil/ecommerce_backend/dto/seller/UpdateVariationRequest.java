package com.nikhil.ecommerce_backend.dto.seller;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
public class UpdateVariationRequest
{
    @PositiveOrZero(message = "Quantity must be 0 or greater")
    private Integer quantityAvailable;

    @Positive(message = "Price must be greater than 0")
    private Double price;

    private Map<String, String> metadata;

    private Boolean isActive;
}