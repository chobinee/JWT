package com.example.jwtserver.controller;

import com.example.jwtserver.dto.MemberDto;
import com.example.jwtserver.dto.LoginRequestDto;
import com.example.jwtserver.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  @GetMapping("/")
  public String getIndex()
  {
    return "home";
  }

  @PostMapping("/join")
  public ResponseEntity join(@RequestBody MemberDto memberDto)
  {
    try {
      memberService.join(memberDto);
      return ResponseEntity.status(HttpStatus.CREATED).body("회원 가입 성공");
    }
    catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 가입 실패");
    }
  }

  @PostMapping("/login")
  public ResponseEntity login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse)
  {
    Map<String, String> tokens = memberService.login(loginRequestDto, httpServletResponse);

    if (tokens.containsKey("code"))
    {
      String value = tokens.get("code");
      if (value.equals("204")) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
      if (value.equals("400")) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호가 틀렸습니다.");
    }

    return ResponseEntity.status(HttpStatus.OK).body(tokens.get("accessToken"));
  }

  @GetMapping("/home")
  public ResponseEntity<MemberDto> home()
  {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    String userId = userDetails.getUsername();

    MemberDto member = memberService.getMemberDtoById(userId);
    if (member == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    return ResponseEntity.status(HttpStatus.OK).body(member);
  }
}
