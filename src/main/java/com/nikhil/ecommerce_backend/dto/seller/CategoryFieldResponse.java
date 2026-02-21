package com.nikhil.ecommerce_backend.dto.seller;

import com.nikhil.ecommerce_backend.dto.admin.MetadataFieldResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryFieldResponse {
    private String name;
    private List<String> values;
}
