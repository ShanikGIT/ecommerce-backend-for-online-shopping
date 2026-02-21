package com.nikhil.ecommerce_backend.entities;

import com.nikhil.ecommerce_backend.constants.ProductRating;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductReview extends BaseEntity
{
    @ManyToOne
    @JoinColumn(name = "customer_user_id")
    private Customer customer;

    private String review;

    @Enumerated(EnumType.STRING)
    private ProductRating rating;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

}
