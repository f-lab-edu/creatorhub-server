package com.creatorhub.constant;

public enum CoinTransactionType {
    CHARGE,  // 코인 증가: 충전
    USE,     // 코인 감소: 사용
    REFUND,  // 코인 감소(또는 조정): 환불
    EXPIRE   // 코인 감소: 기간만료
}
