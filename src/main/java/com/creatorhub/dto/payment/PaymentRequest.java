package com.creatorhub.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentRequest(
        @NotBlank(message = "paymentType이 비어있습니다.")
        String paymentType,

        @NotBlank(message = "paymentKey가 비어있습니다.")
        String paymentKey,

        @NotBlank(message = "orderId가 비어있습니다.")
        String orderId,

        @NotNull(message = "amount가 비어있습니다.")
        @Positive(message = "amount는 1 이상이어야 합니다.")
        Long amount
) { }

