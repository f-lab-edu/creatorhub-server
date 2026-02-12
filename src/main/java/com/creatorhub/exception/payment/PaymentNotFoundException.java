package com.creatorhub.exception.payment;

import com.creatorhub.constant.ErrorCode;

public class PaymentNotFoundException extends PaymentException {
    public PaymentNotFoundException() {
        super(ErrorCode.PAYMENT_NOT_FOUND);
    }
    public PaymentNotFoundException(String message) {
        super(ErrorCode.PAYMENT_NOT_FOUND, message);
    }
}
