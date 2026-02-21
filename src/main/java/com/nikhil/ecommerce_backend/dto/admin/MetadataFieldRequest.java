package com.nikhil.ecommerce_backend.dto.admin;

import lombok.Data;

import java.util.List;

@Data
public class MetadataFieldRequest {
    private Long metadataFieldId;
    private List<String> values;
}
