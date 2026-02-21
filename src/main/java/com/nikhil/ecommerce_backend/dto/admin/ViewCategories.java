package com.nikhil.ecommerce_backend.dto.admin;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ViewCategories
{
    private Long id;
    private String name;
    private Map<String, List<String>> metadata;
}
