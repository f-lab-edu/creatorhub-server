package com.creatorhub.service;

import com.creatorhub.constant.CoinSourceType;
import com.creatorhub.constant.CoinTransactionType;
import com.creatorhub.entity.CoinLedger;
import com.creatorhub.entity.Member;
import com.creatorhub.repository.CoinLedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoinLedgerService {
    private final CoinLedgerRepository coinLedgerRepository;

    public CoinLedger saveChargeByPayment(
            Member member,
            long coinAmountDelta,
            long coinBalanceAfter,
            long paymentId
    ) {
        CoinLedger ledger = CoinLedger.create(
                member,
                CoinTransactionType.CHARGE,
                coinAmountDelta,
                coinBalanceAfter,
                CoinSourceType.PAYMENT,
                paymentId
        );
        return coinLedgerRepository.save(ledger);
    }
}
