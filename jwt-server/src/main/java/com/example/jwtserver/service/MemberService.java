package com.example.jwtserver.service;

import com.example.jwtserver.dto.MemberDto;
import com.example.jwtserver.dto.LoginRequestDto;

public interface MemberService {

  String join(MemberDto memberDto);
  String login(LoginRequestDto loginRequestDto);
}
