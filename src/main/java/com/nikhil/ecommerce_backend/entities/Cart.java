package com.nikhil.ecommerce_backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "customer_user_id", nullable = false)
    private Customer customer;

    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "product_variation_id", nullable = false)
    private ProductVariation productVariation;

    private boolean isWishlistItem;
}