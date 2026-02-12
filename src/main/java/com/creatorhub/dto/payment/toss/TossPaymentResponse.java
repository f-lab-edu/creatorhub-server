package com.creatorhub.dto.payment.toss;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TossPaymentResponse (
    String paymentKey,
    String orderId,
    String status,
    String method,
    Long totalAmount,
    String approvedAt
) { }
