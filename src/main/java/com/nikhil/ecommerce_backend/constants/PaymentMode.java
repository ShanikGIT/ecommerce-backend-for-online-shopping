package com.nikhil.ecommerce_backend.constants;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentMode {

    CREDIT_CARD(1,"Pay using your credit card."),
    DEBIT_CARD(2,"Pay using your debit card."),
    WALLET(3,"Pay with PayPal."),
    CASH_ON_DELIVERY(4, "Use cash on delivery."),
    UPI(5, "Pay using UPI.");

    private final int code;
    private final String message;
}