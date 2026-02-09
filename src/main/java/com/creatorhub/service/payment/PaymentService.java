package com.creatorhub.service.payment;

import com.creatorhub.constant.PaymentStatus;
import com.creatorhub.entity.Member;
import com.creatorhub.entity.Payment;
import com.creatorhub.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public Payment saveFromPgConfirm(
            Member member,
            String orderId,
            String pgProvider,
            String paymentType,
            long amount,
            long coinAmount,
            String paymentKey,
            LocalDateTime approvedAt
    ) {
        Payment payment = Payment.create(
                member,
                orderId,
                pgProvider,
                paymentType,
                amount,
                coinAmount,
                PaymentStatus.PAID,
                paymentKey,
                approvedAt
        );
        return paymentRepository.save(payment);
    }
}
