package com.nikhil.ecommerce_backend.dto.admin;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private List<CategorySummary> parents;
    private List<CategorySummary> immediateChildren;
    private Map<String, List<String>> metadata;
}