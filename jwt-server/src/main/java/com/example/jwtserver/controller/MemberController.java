package com.example.jwtserver.controller;

import com.example.jwtserver.dto.MemberDto;
import com.example.jwtserver.dto.LoginRequestDto;
import com.example.jwtserver.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
  public String join(@RequestBody MemberDto memberDto)
  {
    String result = memberService.join(memberDto);

    if (result.equalsIgnoreCase("success"))
      return "success";
    else return "fail";
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequestDto)
  {
    String result = memberService.login(loginRequestDto);

    if (result.equals("success"))
      return ResponseEntity.ok("success");
    else if (result.equals("404")) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member not found");
    } else if (result.equals("400")) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid password");
    } else {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknown error");
    }
  }
}
