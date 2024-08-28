package com.example.ai01.user.controller;

import com.example.ai01.user.dto.request.MemberRequest;
import com.example.ai01.user.dto.response.MemberResponse;
import com.example.ai01.user.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;


    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse.MemberInfo> getMember(
            @PathVariable Long id) {
        return ResponseEntity.ok().body(memberService.getMemberInfo(id));
    }


    @PostMapping
    public ResponseEntity<MemberResponse.MemberInfo> createMember(
            @RequestBody MemberRequest.CreateMember request) {
        memberService.createMember(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<MemberResponse.AuthResponse> login(@RequestBody MemberRequest.LoginRequest loginRequest) {
        String result = memberService.login(loginRequest);
        MemberResponse.AuthResponse response = MemberResponse.AuthResponse.builder().token(result).build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<MemberResponse.AuthResponse> signUp(@RequestBody MemberRequest.SignUpRequest signUpRequest) {
        String result =memberService.signUp(signUpRequest);
        MemberResponse.AuthResponse response = MemberResponse.AuthResponse.builder().token(result).build();
        return ResponseEntity.ok(response);
    }



}
