package com.nikhil.ecommerce_backend.entities;


import com.nikhil.ecommerce_backend.constants.AddressLabel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address extends BaseEntity
{
    @NotBlank
    private String addressLine;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String country;

    @NotBlank
    private String zipCode;

    @Enumerated(EnumType.STRING)
    private AddressLabel label;

    @ManyToOne
    @JoinColumn(name = "customer_user_id")
    private Customer customer;

    @OneToOne
    @JoinColumn(name = "seller_user_id")
    private Seller seller;

    @Column(name = "is_deleted", nullable = false)
    private boolean removed;
}
