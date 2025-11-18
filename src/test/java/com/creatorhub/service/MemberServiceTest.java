package com.creatorhub.service;

import com.creatorhub.constant.Gender;
import com.creatorhub.constant.Role;
import com.creatorhub.dto.MemberRequestDto;
import com.creatorhub.dto.MemberResponseDto;
import com.creatorhub.entity.Member;
import com.creatorhub.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations="classpath:application-test.properties")
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signupSuccess() {
        // given - 테스트 데이터 준비
        MemberRequestDto memberRequestDto = new MemberRequestDto();
        memberRequestDto.setEmail("test@example.com");
        memberRequestDto.setPassword("test1234!");
        memberRequestDto.setName("홍길동");
        memberRequestDto.setBirthday(LocalDate.of(1990, 1, 15));
        memberRequestDto.setGender(Gender.MALE);

        // when - 회원가입 실행
        MemberResponseDto memberResponseDto = memberService.signup(memberRequestDto);

        // then - 검증
        assertThat(memberResponseDto).isNotNull();
        assertThat(memberResponseDto.getId()).isNotNull();
        assertThat(memberResponseDto.getEmail()).isEqualTo("test@example.com");
        assertThat(memberResponseDto.getName()).isEqualTo("홍길동");
        assertThat(memberResponseDto.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 15));
        assertThat(memberResponseDto.getGender()).isEqualTo(Gender.MALE);
        assertThat(memberResponseDto.getRole()).isEqualTo(Role.MEMBER);  // 기본값 확인
        assertThat(memberResponseDto.getCreatedAt()).isNotNull();

        // DB에 실제로 저장되었는지 확인
        Member savedMember = memberRepository.findById(memberResponseDto.getId()).orElse(null);
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getEmail()).isEqualTo("test@example.com");
    }


    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signupFailDuplicateEmail() {
        // given - 먼저 회원 1명 등록
        MemberRequestDto firstMember = new MemberRequestDto();
        firstMember.setEmail("duplicate@example.com");
        firstMember.setPassword("test1234!");
        firstMember.setName("첫번째회원");
        firstMember.setBirthday(LocalDate.of(1990, 1, 1));
        memberService.signup(firstMember);

        // when - 같은 이메일로 다시 회원가입 시도
        MemberRequestDto duplicateMember = new MemberRequestDto();
        duplicateMember.setEmail("duplicate@example.com");  // 중복!
        duplicateMember.setPassword("test5678!");
        duplicateMember.setName("두번째회원");
        duplicateMember.setBirthday(LocalDate.of(1995, 5, 5));

        // then - 예외 발생 확인
        assertThatThrownBy(() -> memberService.signup(duplicateMember))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 가입된 회원입니다.");
    }


    @Test
    @DisplayName("여러 회원 연속 가입 테스트")
    void signupMultipleMembers() {
        for (int i = 1; i <= 5; i++) {
            MemberRequestDto memberRequestDto = new MemberRequestDto();
            memberRequestDto.setEmail("user" + i + "@example.com");
            memberRequestDto.setPassword("test1234!");
            memberRequestDto.setName("회원" + i);
            memberRequestDto.setBirthday(LocalDate.of(1990 + i, 1, i));
            memberRequestDto.setGender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE);

            memberService.signup(memberRequestDto);
        }

        assertThat(memberRepository.findByEmail("user1@example.com")).isPresent();
        assertThat(memberRepository.findByEmail("user2@example.com")).isPresent();
        assertThat(memberRepository.findByEmail("user3@example.com")).isPresent();
        assertThat(memberRepository.findByEmail("user4@example.com")).isPresent();
        assertThat(memberRepository.findByEmail("user5@example.com")).isPresent();
    }
}
