package com.nikhil.ecommerce_backend.entities;

import com.nikhil.ecommerce_backend.constants.AddressLabel;
import com.nikhil.ecommerce_backend.constants.PaymentMode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity
{
    @ManyToOne
    @JoinColumn(name="customer_user_id")
    private Customer customer;

    private Double amountPaid;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMethod;

    private String customerAddressCity;

    private String customerAddressState;

    private String customerAddressCountry;

    private String customerAddressAddressLine;

    private String customerAddressZipCode;

    @Enumerated(EnumType.STRING)
    private AddressLabel customerAddressLabel;

    @OneToMany(mappedBy = "order")
    private List<OrderProduct> orderProducts = new ArrayList<>();

}
