package com.creatorhub.service.payment;

import com.creatorhub.dto.payment.PaymentRequest;
import com.creatorhub.dto.payment.toss.TossConfirmResponse;
import com.creatorhub.entity.Member;
import com.creatorhub.entity.Payment;
import com.creatorhub.exception.member.MemberNotFoundException;
import com.creatorhub.repository.MemberRepository;
import com.creatorhub.service.CoinLedgerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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

    private final PaymentService paymentService;
    private final CoinLedgerService coinLedgerService;
    private final MemberRepository memberRepository;

    @Transactional
    public TossConfirmResponse tossConfirmAndSave(PaymentRequest req, Long id) {

        // 1. Toss 결제 요청 및 응답
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

        log.info("토스 결제 응답 완료 - getBody(): {}", toss);

        // 2. Payment에 결제 응답 값 저장
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        String pgProvider = "TOSS";
        String paymentType = toss.method();
        LocalDateTime approvedAt =
                OffsetDateTime.parse(toss.approvedAt()).toLocalDateTime();

        // UI 상에서 충전할 수 있는 금액이 정해져 있으므로 코인금액은 단순 계산
        // 100원 = 1코인
        long coinAmount = req.amount()/100;

        Payment payment = paymentService.saveFromPgConfirm(
                member,
                req.orderId(),
                pgProvider,
                paymentType,
                req.amount(),
                coinAmount,
                req.paymentKey(),
                approvedAt
        );

        // 3. CoinLedger에 충전 코인 저장
        long coinBalanceAfter = member.addCoinAndGetBalance(coinAmount);

        coinLedgerService.saveChargeByPayment(
                member,
                coinAmount,
                coinBalanceAfter,
                payment.getId()
        );

        return toss;
    }
}
