package com.creatorhub.exception.payment;

import com.creatorhub.constant.ErrorCode;

public class PaymentAmountMismatchException extends PaymentException {
    public PaymentAmountMismatchException() {
        super(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
    }
    public PaymentAmountMismatchException(String message) {
        super(ErrorCode.PAYMENT_AMOUNT_MISMATCH, message);
    }
}
