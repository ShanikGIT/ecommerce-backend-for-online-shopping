package com.nikhil.ecommerce_backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryMetadataValue
{
    private String fieldName;
    private String values;
}