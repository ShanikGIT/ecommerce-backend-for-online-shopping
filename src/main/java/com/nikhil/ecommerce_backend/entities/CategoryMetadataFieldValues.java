package com.nikhil.ecommerce_backend.entities;


import com.nikhil.ecommerce_backend.helper.ValuesAttributeConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "category_metadata_field_values")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMetadataFieldValues extends BaseEntity
{
    @ManyToOne
    @JoinColumn(name = "category_metadata_field_id")
    private CategoryMetadataField categoryMetaDataField;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Convert(converter = ValuesAttributeConverter.class)
    private List<String> value;
}
