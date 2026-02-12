package com.creatorhub.controller;

import com.creatorhub.dto.payment.PaymentOrderRequest;
import com.creatorhub.dto.payment.PaymentOrderResponse;
import com.creatorhub.dto.payment.PaymentRequest;
import com.creatorhub.dto.payment.PaymentResponse;
import com.creatorhub.security.auth.CustomUserPrincipal;
import com.creatorhub.service.payment.PaymentConfirmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentConfirmService paymentConfirmService;

    /**
     * 결제 주문 생성 (토스 결제창 호출 전)
     * 서버가 먼저 orderId와 기대 금액을 저장해두어 이후 금액 위변조를 방지합니다.
     */
    @PostMapping("/orders")
    public ResponseEntity<PaymentOrderResponse> createOrder(
            @Valid @RequestBody PaymentOrderRequest req,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        PaymentOrderResponse paymentOrderResponse =
                paymentConfirmService.createOrder(req, principal.id());
        return ResponseEntity.ok(paymentOrderResponse);
    }

    /**
     * 결제 confirm (토스 결제창 완료 후)
     */
    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(
            @Valid @RequestBody PaymentRequest req,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        PaymentResponse paymentResponse =
                paymentConfirmService.confirmAndSave(req, principal.id());
        return ResponseEntity.ok(paymentResponse);
    }
}
