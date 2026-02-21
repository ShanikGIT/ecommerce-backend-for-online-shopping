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
@Table(name = "sellers")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seller extends User
{
    private String gst;

    @Column(unique = true, nullable = false)
    private String companyContact;

    private String companyName;

    private String imageUrl;

    @OneToMany(mappedBy = "seller")
    private List<Product> products = new ArrayList<>();

    @OneToOne(mappedBy = "seller", cascade = CascadeType.ALL)
    private Address address;
}
