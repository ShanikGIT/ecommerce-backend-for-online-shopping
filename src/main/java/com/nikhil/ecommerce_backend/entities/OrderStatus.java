package com.nikhil.ecommerce_backend.entities;


import com.nikhil.ecommerce_backend.constants.Status;
import com.nikhil.ecommerce_backend.constants.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatus extends BaseEntity
{
    @ManyToOne
    @JoinColumn(name = "order_product_id")
    private OrderProduct orderProduct;

    @Enumerated(EnumType.STRING)
    private Status fromStatus;

    @Enumerated(EnumType.STRING)
    private Status toStatus;

    private String transitionNotesComments;

    private LocalDateTime transitionDate;
}
