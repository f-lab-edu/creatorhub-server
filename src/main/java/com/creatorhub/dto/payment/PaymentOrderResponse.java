package com.creatorhub.dto.payment;

public record PaymentOrderResponse(
        String orderId,
        Long amount
) { }
