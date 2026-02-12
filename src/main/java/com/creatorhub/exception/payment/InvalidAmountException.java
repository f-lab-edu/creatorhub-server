package com.creatorhub.exception.payment;

import com.creatorhub.constant.ErrorCode;

public class InvalidAmountException extends PaymentException {
    public InvalidAmountException() {
        super(ErrorCode.INVALID_AMOUNT);
    }
    public InvalidAmountException(String message) {
        super(ErrorCode.INVALID_AMOUNT, message);
    }
}
