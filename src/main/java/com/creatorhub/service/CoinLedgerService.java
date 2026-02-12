package com.creatorhub.service;

import com.creatorhub.constant.CoinSourceType;
import com.creatorhub.constant.CoinTransactionType;
import com.creatorhub.entity.CoinLedger;
import com.creatorhub.entity.Member;
import com.creatorhub.repository.CoinLedgerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoinLedgerService {
    private final CoinLedgerRepository coinLedgerRepository;

    public CoinLedger saveChargeByPayment(
            Member member,
            long coinAmountDelta,
            long coinBalanceAfter,
            CoinSourceType sourceType,
            long sourceId
    ) {
        try {

            return coinLedgerRepository.saveAndFlush(
                    CoinLedger.create(
                            member,
                            CoinTransactionType.CHARGE,
                            coinAmountDelta,
                            coinBalanceAfter,
                            sourceType,
                            sourceId
                    )
            );

        } catch (DataIntegrityViolationException e) {
            // UNIQUE(source_type, source_id) 제약 조건 위반시 -> 멱등성 유지
            log.warn("CoinLedger 중복 생성 시도 sourceType={}, sourceId={}", sourceType, sourceId);
            return null;
        }
    }
}
