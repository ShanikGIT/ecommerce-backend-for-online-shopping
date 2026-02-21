package com.nikhil.ecommerce_backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderProduct extends BaseEntity
{
    @ManyToOne
    private Order order;

    @ManyToOne
    private ProductVariation productVariation;

    private Integer quantity;

    private Double price;

    @OneToMany(mappedBy = "orderProduct")
    private List<OrderStatus> statuses = new ArrayList<>();
}
