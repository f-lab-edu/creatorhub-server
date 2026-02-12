package com.creatorhub.exception.payment;

import com.creatorhub.constant.ErrorCode;

public class InsufficientCoinException extends PaymentException {
    public InsufficientCoinException() {
        super(ErrorCode.INSUFFICIENT_COIN);
    }
    public InsufficientCoinException(String message) {
        super(ErrorCode.INSUFFICIENT_COIN, message);
    }
}
