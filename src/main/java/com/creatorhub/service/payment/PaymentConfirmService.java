package com.creatorhub.service.payment;

import com.creatorhub.constant.PaymentStatus;
import com.creatorhub.dto.payment.PaymentOrderRequest;
import com.creatorhub.dto.payment.PaymentOrderResponse;
import com.creatorhub.dto.payment.PaymentRequest;
import com.creatorhub.dto.payment.PaymentResponse;
import com.creatorhub.dto.payment.toss.TossConfirmResponse;
import com.creatorhub.dto.payment.toss.TossPaymentResponse;
import com.creatorhub.entity.Member;
import com.creatorhub.entity.Payment;
import com.creatorhub.exception.member.MemberNotFoundException;
import com.creatorhub.exception.payment.PaymentConfirmFailedException;
import com.creatorhub.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentConfirmService {
    @Value("${toss.secret-key}")
    private String tossSecretKey;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String CONFIRM_URL =
            "https://api.tosspayments.com/v1/payments/confirm";
    private static final String PAYMENT_GET_URL =
            "https://api.tosspayments.com/v1/payments/{paymentKey}";

    private final PaymentService paymentService;
    private final MemberRepository memberRepository;


    /**
     * 결제 주문 생성
     */
    public PaymentOrderResponse createOrder(PaymentOrderRequest req, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
        return paymentService.createOrder(req, member);
    }

    /**
     * 토스 결제 요청 및 payment, coin_ledger 테이블에 save
     * Toss PG 결제가 있으므로 @Transactional 사용 하지 X
     */
    public PaymentResponse confirmAndSave(PaymentRequest req, Long id) {

        // 1. 결제 요청시 payment 테이블에 결과 반영
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        // coinAmount 계산
        // 충전금액은 UI에서 정해진 금액을 선택하게 되어있으므로 단순 계산
        // 100원 = 1코인
        long coinAmount = req.amount() / 100;

        // 서버에 저장된 주문이 있으면 금액 검증 후 사용,
        // 없으면 신규 PENDING 생성
        Payment payment = paymentService.findAndValidateAmountOrCreate(member, req, coinAmount);

        // 이미 PAID(결제상태)면 -> 그냥 반환(멱등성)
        if (payment.getStatus() == PaymentStatus.PAID) {
            log.info("이미 처리된 결제입니다. orderId={}", req.orderId());

            return new PaymentResponse(
                    payment.getOrderId(),
                    payment.getStatus(),
                    payment.getAmount(),
                    payment.getCoinAmount(),
                    payment.getApprovedAt()
            );
        }

        try {
            // 2. 토스에 결제 요청
            TossConfirmResponse toss = callTossConfirm(req);

            // 3. 토스 결제 성공 후 payment, coin_ledger 테이블에 결과 반영
            return paymentService.afterConfirmSave(toss, member, payment);

        } catch (Exception e) {
            log.error("토스 결제 요청 중 실패 orderId={}", req.orderId(), e);
            paymentService.markFailed(payment);
            throw new PaymentConfirmFailedException("토스 결제 요청 중 실패했습니다.");
        }
    }

    /**
     * 토스 결제 요청
     */
    private TossConfirmResponse callTossConfirm(PaymentRequest req) {
        String auth = "Basic " + Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, auth);

        String confirmBody = """
            {"paymentKey":"%s","orderId":"%s","amount":%d}
            """.formatted(req.paymentKey(), req.orderId(), req.amount());

        HttpEntity<String> entity = new HttpEntity<>(confirmBody, headers);

        ResponseEntity<TossConfirmResponse> tossRes = restTemplate.exchange(
                CONFIRM_URL,
                HttpMethod.POST,
                entity,
                TossConfirmResponse.class
        );

        TossConfirmResponse toss = tossRes.getBody();

        if (toss == null) {
            throw new IllegalStateException("토스 confirm 응답이 비어있습니다.");
        }

        log.debug("토스 결제 응답 완료 - getBody(): {}", toss);
        return toss;
    }


    /**
     * 토스 결제 내역 확인
     */
    public TossPaymentResponse callTossGetPayment(String paymentKey) {
        String auth = "Basic " + Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, auth);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<TossPaymentResponse> tossRes = restTemplate.exchange(
                PAYMENT_GET_URL,
                HttpMethod.GET,
                entity,
                TossPaymentResponse.class,
                paymentKey
        );

        TossPaymentResponse toss = tossRes.getBody();
        if (toss == null) {
            throw new IllegalStateException("토스 결제 조회 응답이 비어있습니다.");
        }

        log.debug("토스 결제 조회 완료: {}", toss);
        return toss;
    }
}
