package com.creatorhub.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentOrderRequest(
        @NotNull(message = "amount가 비어있습니다.")
        @Positive(message = "amount는 1 이상이어야 합니다.")
        Long amount
) { }
