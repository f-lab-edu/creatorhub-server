package com.creatorhub.service;

import com.creatorhub.dto.MemberRequestDto;
import com.creatorhub.dto.MemberResponseDto;
import com.creatorhub.entity.Member;
import com.creatorhub.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     */
    public MemberResponseDto signup(MemberRequestDto memberRequestDto) {
        // 이메일 중복 체크
        validateDuplicateMember(memberRequestDto);

        Member member = Member.builder()
                .email(memberRequestDto.getEmail())
                .password(passwordEncoder.encode(memberRequestDto.getPassword()))
                .name(memberRequestDto.getName())
                .birthday(memberRequestDto.getBirthday())
                .gender(memberRequestDto.getGender())
                .build();

        Member savedMember = memberRepository.save(member);

        log.info("회원가입 완료 - email: {}, id: {}", savedMember.getEmail(), savedMember.getId());

        return MemberResponseDto.from(savedMember);
    }


    /**
     * 이메일로 회원 중복 체크
     */
    private void validateDuplicateMember(MemberRequestDto memberRequestDto) {
        memberRepository.findByEmail(memberRequestDto.getEmail())
            .ifPresent(member -> {
                throw new IllegalArgumentException("이미 가입된 회원입니다.");
            });
    }
}
