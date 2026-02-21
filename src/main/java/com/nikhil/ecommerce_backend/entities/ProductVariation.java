package com.nikhil.ecommerce_backend.entities;


import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "product_variations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariation extends BaseEntity
{
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @NotNull
    @PositiveOrZero
    private Integer quantityAvailable;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double price;

    private String primaryImageName;
    private boolean isActive;
    private boolean removed;

    @OneToMany(mappedBy = "productVariation")
    private List<OrderProduct> orderProducts = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private JsonNode metadata;

    @OneToMany(mappedBy = "productVariation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductImage> secondaryImages = new HashSet<>();

    @OneToMany(mappedBy = "productVariation")
    private List<Cart>  carts = new ArrayList<>();
}
