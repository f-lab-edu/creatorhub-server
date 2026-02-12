package com.creatorhub.constant;

public enum PaymentStatus {
    PENDING,   // 결제 생성됨. 아직 결제 승인 전 상태 (confirm 전)
    PAID,      // 결제 승인 완료. 실제 결제가 정상적으로 완료된 상태
    FAILED,    // 결제 승인 실패. 카드 한도 초과, 인증 실패 등으로 결제 실패
    CANCELED,  // 결제 취소됨. 사용자 취소 또는 관리자 취소
    REFUND     // 결제 환불 완료. 승인된 결제가 환불 처리된 상태
}
