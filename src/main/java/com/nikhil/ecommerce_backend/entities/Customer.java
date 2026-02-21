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
@Table(name = "customers")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends User
{
    private String contact;

    @OneToMany(mappedBy = "customer")
    private Set<ProductReview> reviews;

    @OneToOne(mappedBy = "customer")
    private Cart cart;

    private String imageUrl;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders= new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Address> addresses = new ArrayList<>();

}
