package com.creatorhub.dto.payment;

import com.creatorhub.constant.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentResponse (
        String orderId,              // 주문 식별자
        PaymentStatus status,        // 우리 서비스 기준 결제 상태
        Long amount,              // 결제 금액
        Long coinAmount,             // 지급된 코인 수
        LocalDateTime approvedAt     // 결제 승인 시각 (성공 시)
) {}