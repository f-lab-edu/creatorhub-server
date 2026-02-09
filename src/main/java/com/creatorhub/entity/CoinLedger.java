package com.creatorhub.entity;

import com.creatorhub.constant.CoinSourceType;
import com.creatorhub.constant.CoinTransactionType;
import com.creatorhub.entity.base.BaseSoftDeleteEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(
        name = "coin_ledger",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_coin_ledger_source",
                        columnNames = {"source_type", "source_id"}
                )
        },
        indexes = {
                @Index(name = "idx_coin_ledger_member_created", columnList = "member_id, created_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE coin_ledger SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class CoinLedger extends BaseSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30, columnDefinition = "VARCHAR(30)")
    private CoinTransactionType transactionType;

    @Column(nullable = false)
    private Long coinAmountDelta;

    @Column(nullable = false)
    private Long coinBalanceAfter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30, columnDefinition = "VARCHAR(30)")
    private CoinSourceType sourceType;

    // source pk (paymentId, episode_access_id, event_id 등)
    @Column(nullable = false)
    private Long sourceId;

    @Builder(access = AccessLevel.PRIVATE)
    private CoinLedger(Member member,
                       CoinTransactionType transactionType,
                       Long coinAmountDelta,
                       Long coinBalanceAfter,
                       CoinSourceType sourceType,
                       Long sourceId) {
        this.member = member;
        this.transactionType = transactionType;
        this.coinAmountDelta = coinAmountDelta;
        this.coinBalanceAfter = coinBalanceAfter;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
    }

    public static CoinLedger create(Member member,
                                    CoinTransactionType transactionType,
                                    long coinAmountDelta,
                                    long coinBalanceAfter,
                                    CoinSourceType sourceType,
                                    long sourceId) {
        return CoinLedger.builder()
                .member(member)
                .transactionType(transactionType)
                .coinAmountDelta(coinAmountDelta)
                .coinBalanceAfter(coinBalanceAfter)
                .sourceType(sourceType)
                .sourceId(sourceId)
                .build();
    }
}