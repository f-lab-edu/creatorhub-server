package com.creatorhub.service;

import com.creatorhub.dto.MemberRequest;
import com.creatorhub.dto.MemberResponse;
import com.creatorhub.entity.Member;
import com.creatorhub.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     */
    @Transactional
    public MemberResponse signup(MemberRequest memberRequest) {
        // 이메일 중복 체크
        validateDuplicateMember(memberRequest);

        String encodedPassword = passwordEncoder.encode(memberRequest.password());

        Member member = Member.createMember(
                memberRequest.email(),
                encodedPassword,
                memberRequest.name(),
                memberRequest.birthday(),
                memberRequest.gender()
        );

        Member savedMember = memberRepository.save(member);

        log.info("회원가입 완료 - email: {}, id: {}", savedMember.getEmail(), savedMember.getId());

        return MemberResponse.from(savedMember);
    }


    /**
     * 이메일로 회원 중복 체크
     */
    private void validateDuplicateMember(MemberRequest memberRequest) {
        memberRepository.findByEmail(memberRequest.email())
            .ifPresent(member -> {
                throw new IllegalArgumentException("이미 가입된 회원입니다.");
            });
    }

    /**
     * 회원삭제
     */
    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        memberRepository.delete(member);
        log.info("회원 삭제 완료 - email: {}, id: {}", member.getEmail(), member.getId());
    }
}
