package com.creatorhub.controller;

import com.creatorhub.dto.payment.PaymentRequest;
import com.creatorhub.dto.payment.toss.TossConfirmResponse;
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

    @PostMapping(value = "/confirm")
    public ResponseEntity<TossConfirmResponse> confirmPayment(
            @Valid @RequestBody PaymentRequest req,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        TossConfirmResponse tossConfirmResponse =
                paymentConfirmService.tossConfirmAndSave(req, principal.id());
        return ResponseEntity.ok(tossConfirmResponse);
    }
}
