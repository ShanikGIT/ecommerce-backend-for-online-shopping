package com.nikhil.ecommerce_backend.dto.admin;

import lombok.Builder;
import lombok.Data;
import com.nikhil.ecommerce_backend.entities.CategoryMetadataField;

@Data
@Builder
public class MetadataFieldResponse
{
    private Long id;
    private String name;
}