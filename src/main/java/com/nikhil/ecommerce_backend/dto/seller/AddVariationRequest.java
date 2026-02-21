package com.nikhil.ecommerce_backend.dto.seller;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.Map;

@Data
public class AddVariationRequest {

    @NotNull(message = "Product ID is mandatory")
    private Long productId;

    @NotNull(message = "Quantity is mandatory")
    @PositiveOrZero
    private Integer quantityAvailable;

    @NotNull(message = "Price is mandatory")
    @Positive(message = "Price must be greater than 0")
    private Double price;

    @NotEmpty(message = "Metadata is mandatory and must have at least one field-value pair")
    private Map<String, String> metadata;
}