package com.creatorhub.service.payment;

import com.creatorhub.constant.CoinSourceType;
import com.creatorhub.constant.PaymentStatus;
import com.creatorhub.dto.payment.PaymentOrderRequest;
import com.creatorhub.dto.payment.PaymentOrderResponse;
import com.creatorhub.dto.payment.PaymentRequest;
import com.creatorhub.dto.payment.PaymentResponse;
import com.creatorhub.dto.payment.toss.TossConfirmResponse;
import com.creatorhub.dto.payment.toss.TossPaymentResponse;
import com.creatorhub.entity.CoinLedger;
import com.creatorhub.entity.Member;
import com.creatorhub.entity.Payment;
import com.creatorhub.exception.payment.PaymentAmountMismatchException;
import com.creatorhub.exception.payment.PaymentConfirmFailedException;
import com.creatorhub.exception.payment.PaymentNotFoundException;
import com.creatorhub.repository.MemberRepository;
import org.springframework.dao.DataIntegrityViolationException;
import com.creatorhub.repository.PaymentRepository;
import com.creatorhub.service.CoinLedgerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final CoinLedgerService coinLedgerService;
    private final PaymentConfirmService paymentConfirmService;

    /**
     * 결제 주문 생성 - 서버가 orderId와 기대 금액을 DB에 먼저 저장
     */
    public PaymentOrderResponse createOrder(PaymentOrderRequest req, Member member) {
        String orderId = UUID.randomUUID().toString();
        long coinAmount = req.amount() / 100;

        paymentRepository.save(
                Payment.create(
                        member,
                        orderId,
                        "UNKNOWN",
                        "UNKNOWN",
                        req.amount(),
                        coinAmount,
                        PaymentStatus.PENDING,
                        null,
                        null
                )
        );

        return new PaymentOrderResponse(orderId, req.amount());
    }

    /**
     * orderId로 기존 주문을 찾아 금액 검증 후 반환
     * 주문이 없으면 신규 PENDING 생성,
     * 동시 요청으로 DataIntegrityViolationException 발생 시 재조회로 멱등 처리
     */
    @Transactional
    public Payment findAndValidateAmountOrCreate(Member member, PaymentRequest req, long coinAmount) {
        return paymentRepository.findByOrderId(req.orderId())
                .map(payment -> {
                    if (!payment.getAmount().equals(req.amount())) {
                        log.warn("금액 불일치 감지 orderId={}, 서버기대금액={}, 클라이언트요청금액={}",
                                req.orderId(), payment.getAmount(), req.amount());
                        throw new PaymentAmountMismatchException();
                    }
                    return payment;
                })
                .orElseGet(() -> saveNewPending(member, req, coinAmount));
    }

    /**
     * 신규 PENDING Payment 저장
     * 동시 요청으로 유니크 제약 위반 시 DataIntegrityViolationException을 잡아
     * 이미 저장된 row를 재조회해 반환 (멱등성 보장)
     */
    private Payment saveNewPending(Member member, PaymentRequest req, long coinAmount) {
        try {
            return paymentRepository.saveAndFlush(
                    Payment.create(
                            member,
                            req.orderId(),
                            "UNKNOWN",
                            "UNKNOWN",
                            req.amount(),
                            coinAmount,
                            PaymentStatus.PENDING,
                            null,
                            null
                    )
            );
        } catch (DataIntegrityViolationException e) {
            // 동시 요청으로 uk_payment_order_id 위반 → 이미 저장된 row 반환
            log.warn("PENDING 중복 생성 시도, 기존 row 반환 orderId={}", req.orderId());
            return paymentRepository.findByOrderId(req.orderId())
                    .orElseThrow(PaymentNotFoundException::new);
        }
    }


    /**
     * PG confirm 성공 응답을 Payment에 반영
     */
    public Payment applyPaid(Payment payment,
                                     String pgProvider,
                                     String paymentType,
                                     String paymentKey,
                                     String tossStatus,
                                     LocalDateTime approvedAt) {

        // 이미 처리됐으면 아무 것도 하지 않음
        if (payment.getStatus() == PaymentStatus.PAID) {
            return payment;
        }

        payment.markPaid(pgProvider, paymentType, paymentKey, tossStatus, approvedAt);
        return payment;
    }

    /**
     * Payment status FAILED 상태로 변경
     */
    @Transactional
    public void markFailed(Payment payment) {
        payment.markFailed();
        paymentRepository.save(payment); // detached -> merge
    }

    /**
     * Payment, CoinLedger 최종 save
     */
    @Transactional
    public PaymentResponse afterConfirmSave(TossConfirmResponse toss, Member member, Payment payment) {

        // Toss status 검증
        if (!"DONE".equalsIgnoreCase(toss.status())) {
            log.warn("결제가 승인되지 않았습니다. Toss Status={}", toss.status());
            throw new PaymentConfirmFailedException("결제가 승인되지 않았습니다.");
        }

        // Payment에 승인 정보 반영
        Payment paidPayment = null;

        paidPayment = applyPaid(
                payment,
                "TOSS",
                toss.method(),
                toss.paymentKey(),
                toss.status(),
                OffsetDateTime.parse(toss.approvedAt()).toLocalDateTime()
        );


        // PAID가 아니라면 토스에 결제 내역 재확인 요청
        if(paidPayment.getStatus() != PaymentStatus.PAID) {
            TossPaymentResponse tossPaymentResponse =
                    paymentConfirmService.callTossGetPayment(toss.paymentKey());

            paidPayment = applyPaid(
                    payment,
                    "TOSS",
                    toss.method(),
                    toss.paymentKey(),
                    toss.status(),
                    OffsetDateTime.parse(toss.approvedAt()).toLocalDateTime()
            );

        }

        // detached -> merge
        paymentRepository.save(paidPayment);

        // member 테이블의 코인 잔액 반영
        // 이미 지급했는지 체크가 없으면 재시도 시 member테이블의 coin_balance가 또 올라감.
        // 따라서 ledger insert 성공한 경우에만 coin_balance가 업데이트
        long currentBalance = member.getCoinBalance();
        long coinAmount = paidPayment.getCoinAmount();
        long balanceAfter = currentBalance + coinAmount;

        CoinLedger savedCoinLedger = coinLedgerService.saveChargeByPayment(
                member,
                coinAmount,
                balanceAfter,
                CoinSourceType.PAYMENT,
                paidPayment.getId()
        );

        if (savedCoinLedger != null) {
            memberRepository.increaseCoinBalance(member.getId(), coinAmount);
        }

        return new PaymentResponse(
                paidPayment.getOrderId(),
                paidPayment.getStatus(),
                paidPayment.getAmount(),
                paidPayment.getCoinAmount(),
                paidPayment.getApprovedAt()
        );
    }
}
