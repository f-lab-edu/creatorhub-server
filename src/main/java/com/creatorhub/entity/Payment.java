package com.creatorhub.entity;

import com.creatorhub.constant.PaymentStatus;
import com.creatorhub.entity.base.BaseSoftDeleteEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_payment_order_id", columnNames = "order_id"),
                @UniqueConstraint(name = "uk_payment_payment_key", columnNames = "payment_key")
        },
        indexes = {
                @Index(name = "idx_payment_member_id", columnList = "member_id"),
                @Index(name = "idx_payment_created_at ", columnList = "created_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE payment SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Payment extends BaseSoftDeleteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 64)
    private String orderId;

    @Column(nullable = false, length = 30)
    private String pgProvider;

    @Column(nullable = false, length = 30)
    private String paymentType;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private Long coinAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30, columnDefinition = "VARCHAR(30)")
    private PaymentStatus status;

    @Column(length = 100)
    private String paymentKey;

    private LocalDateTime approvedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Payment(Member member,
                    String orderId,
                    String pgProvider,
                    String paymentType,
                    Long amount,
                    Long coinAmount,
                    PaymentStatus status,
                    String paymentKey,
                    LocalDateTime approvedAt) {
        this.member = member;
        this.orderId = orderId;
        this.pgProvider = pgProvider;
        this.paymentType = paymentType;
        this.amount = amount;
        this.coinAmount = coinAmount;
        this.status = status;
        this.paymentKey = paymentKey;
        this.approvedAt = approvedAt;
    }

    public static Payment create(
            Member member,
            String orderId,
            String pgProvider,
            String paymentType,
            Long amount,
            Long coinAmount,
            PaymentStatus status,
            String paymentKey,
            LocalDateTime approvedAt
    ) {
        return Payment.builder()
                .member(member)
                .orderId(orderId)
                .pgProvider(pgProvider)
                .paymentType(paymentType)
                .amount(amount)
                .coinAmount(coinAmount)
                .status(status)
                .paymentKey(paymentKey)
                .approvedAt(approvedAt)
                .build();
    }

    public void markPaid(String pgProvider, String paymentType, String paymentKey, String tossStatus, LocalDateTime approvedAt) {
        this.pgProvider = pgProvider;
        this.paymentType = paymentType;
        this.paymentKey = paymentKey;
        this.status = applyTossStatus(tossStatus);
        this.approvedAt = approvedAt;
    }

    public void markFailed() {
        this.status = PaymentStatus.FAILED;
    }


    // 토스 status에 따른 Payment status 변경
    // status 정책은 docs/coin_payment.md 문서 참고
    public PaymentStatus applyTossStatus(String tossStatus) {

        switch (tossStatus) {
            case "READY", "IN_PROGRESS", "WAITING_FOR_DEPOSIT" -> {
                return PaymentStatus.PENDING;
            }
            case "DONE" -> {
                return PaymentStatus.PAID;
            }
            case "ABORTED", "EXPIRED" -> {
                return PaymentStatus.FAILED;
            }
            case "CANCELED" -> {
                return PaymentStatus.CANCELED;
            }
            case "PARTIAL_CANCELED" -> {
                // 부분 취소(우리 도메인에서는 환불)
                return PaymentStatus.REFUND;
            }
            default -> {
                // 아직 확정 아님, PENDING 유지
                return PaymentStatus.PENDING;
            }
        }
    }
}
