package com.creatorhub.exception.payment;

import com.creatorhub.constant.ErrorCode;

public class PaymentConfirmFailedException extends PaymentException {
    public PaymentConfirmFailedException() {
        super(ErrorCode.PAYMENT_CONFIRM_FAIL);
    }
    public PaymentConfirmFailedException(String message) {
        super(ErrorCode.PAYMENT_CONFIRM_FAIL, message);
    }
}
