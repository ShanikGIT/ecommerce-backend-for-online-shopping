package com.nikhil.ecommerce_backend.constants;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Status
{
    ORDER_PLACED(1, "Order placed"),
    ORDER_CONFIRMED(2, "Order confirmed"),
    ORDER_REJECTED(3, "Order rejected"),
    CANCELLED(4, "Order cancelled"),
    ORDER_SHIPPED(5, "Order shipped"),
    DELIVERED(6, "Order delivered"),
    RETURN_REQUESTED(7, "Return requested"),
    RETURN_APPROVED(8, "Return approved"),
    RETURN_REJECTED(9, "Return rejected"),
    PICK_UP_INITIATED(10, "Pickup initiated"),
    PICK_UP_COMPLETED(11, "Pickup completed"),
    REFUND_INITIATED(12, "Refund initiated"),
    REFUND_COMPLETED(13, "Refund completed"),
    CLOSED(14, "Order closed");
    private final int code;
    private final String description;
}
