package com.creatorhub.repository;

import com.creatorhub.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    // 정합성을 위해 coinBalance 원자적 업데이트
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Member m
        SET m.coinBalance = m.coinBalance + :coinAmount
        WHERE m.id = :memberId
    """)
    void increaseCoinBalance(@Param("memberId") Long memberId,
                            @Param("coinAmount") long coinAmount);
}
