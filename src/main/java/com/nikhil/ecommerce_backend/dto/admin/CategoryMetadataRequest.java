package com.nikhil.ecommerce_backend.dto.admin;

import lombok.Data;

import java.util.List;

@Data
public class CategoryMetadataRequest {
    private Long categoryId;
    private List<MetadataFieldRequest> metadata;
}