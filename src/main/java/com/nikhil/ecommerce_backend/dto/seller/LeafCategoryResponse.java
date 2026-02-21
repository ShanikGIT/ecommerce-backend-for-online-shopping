package com.nikhil.ecommerce_backend.dto.seller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeafCategoryResponse {
    private Long id;
    private String name;
    private List<String> parentChain;
    private List<CategoryFieldResponse> metadataFields;
}
