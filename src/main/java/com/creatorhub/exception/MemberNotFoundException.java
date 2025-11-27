package com.creatorhub.exception;

import com.creatorhub.constant.ErrorCode;

public class MemberNotFoundException extends MemberException {
    public MemberNotFoundException() {
        super(ErrorCode.MEMBER_NOT_FOUND);
    }

    public MemberNotFoundException(String message) {
        super(ErrorCode.MEMBER_NOT_FOUND, message);
    }
}
