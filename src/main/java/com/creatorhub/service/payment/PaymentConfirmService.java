package com.creatorhub.service.payment;

import com.creatorhub.dto.payment.PaymentRequest;
import com.creatorhub.dto.payment.toss.TossConfirmResponse;
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

    public TossConfirmResponse confirm(PaymentRequest req) {

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

        TossConfirmResponse tossConfirmResponse = tossRes.getBody();

        if (tossConfirmResponse == null) {
            throw new IllegalStateException("토스 confirm 응답이 비어있습니다.");
        }

        log.debug("토스 결제 응답 완료 - getBody(): {}", tossConfirmResponse);

        return tossConfirmResponse;
    }
}
