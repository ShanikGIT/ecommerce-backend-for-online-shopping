package com.nikhil.ecommerce_backend.dto.seller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class AddProductRequest {

    @NotBlank(message = "Product name is mandatory")
    private String name;

    @NotBlank(message = "Brand name is mandatory")
    private String brand;

    @NotNull(message = "Category ID is mandatory")
    @Min(value = 1, message = "Invalid Category ID")
    private Long categoryId;

    private String description;

    private boolean isCancellable;

    private boolean isReturnable;
}