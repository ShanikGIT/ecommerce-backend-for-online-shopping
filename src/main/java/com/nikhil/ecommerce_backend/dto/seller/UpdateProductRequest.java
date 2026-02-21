package com.nikhil.ecommerce_backend.dto.seller;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProductRequest {

    @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
    private String name;

    private String description;

    private Boolean isCancellable;

    private Boolean isReturnable;
}
