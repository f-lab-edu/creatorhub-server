package com.creatorhub.repository;

import com.creatorhub.entity.CoinLedger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinLedgerRepository extends JpaRepository<CoinLedger, Long> {
}
