package com.example.ai01.user.service;

import com.example.ai01.security.util.JwtUtil;
import com.example.ai01.user.dto.request.MemberRequest;
import com.example.ai01.user.dto.response.MemberResponse;
import com.example.ai01.user.entity.Member;
import com.example.ai01.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static com.example.ai01.user.entity.Member.DTOtoEntity;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public MemberResponse.MemberInfo getMemberInfo(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + id));

        return MemberResponse.MemberInfo
                .builder()
                .id(id)
                .name(member.getName())
                .name(member.getUsername())
                .nickname(member.getNickname())
                .phone(member.getPhone())
                .email(member.getEmail())
                .build();
    }

    @Transactional(readOnly = true)
    public MemberResponse.MemberInfo getMemberInfoByUsername(String username) {
        // 유저 프로필 조회 로직
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return MemberResponse.MemberInfo
                .builder()
                .id(member.getId())
                .name(member.getName())
                .name(member.getUsername())
                .nickname(member.getNickname())
                .phone(member.getPhone())
                .email(member.getEmail())
                .build();
    }


    @Transactional
    public void createMember(MemberRequest.CreateMember request) {
        Member member = Member.DTOtoEntity(request);
        memberRepository.save(member);

    }


    @Transactional
    public String signUp(MemberRequest.SignUpRequest request) {
        // 회원가입 로직 구현
        if (memberRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        Member member = new Member();
        member.setUsername(request.getUsername());
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        memberRepository.save(member);

        return "User registered successfully";
    }

    @Transactional
    public String login(MemberRequest.LoginRequest request) {
        // 로그인 로직 구현
        Member member = memberRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        // 로그인 성공 시 JWT 토큰 발급
        return jwtUtil.generateToken(member.getUsername());
    }




}
