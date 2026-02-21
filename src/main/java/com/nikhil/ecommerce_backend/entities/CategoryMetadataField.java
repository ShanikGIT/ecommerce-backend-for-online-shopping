package com.nikhil.ecommerce_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMetadataField extends BaseEntity
{
    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "categoryMetaDataField")
    private Set<CategoryMetadataFieldValues> categoryMetadataFieldVales = new HashSet<CategoryMetadataFieldValues>();
}
