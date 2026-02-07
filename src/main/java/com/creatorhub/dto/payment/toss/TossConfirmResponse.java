package com.creatorhub.dto.payment.toss;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TossConfirmResponse(
        String paymentKey,
        String orderId,
        String status,        // DONE, CANCELED, etc
        String method,        // 카드, 계좌이체 등
        Long totalAmount,     // 결제금액
        String approvedAt     // ISO 문자열
) {
}