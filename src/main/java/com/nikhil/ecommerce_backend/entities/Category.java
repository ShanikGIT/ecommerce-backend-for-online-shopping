package com.nikhil.ecommerce_backend.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseEntity
{
    private String name;

    @ManyToOne
    private Category parentCategory;

    @OneToMany(mappedBy = "category")
    private Set<CategoryMetadataFieldValues> categoryMetadataFieldVales = new HashSet<CategoryMetadataFieldValues>();

    @OneToMany(mappedBy = "category")
    private List<Product> products =  new ArrayList<>();

    private boolean isCancellable;
    private boolean isReturnable;
    private boolean isActive;
    private  boolean isDeleted;
}
