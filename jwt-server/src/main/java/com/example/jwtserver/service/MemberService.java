package com.example.jwtserver.service;

import com.example.jwtserver.dto.MemberDto;
import com.example.jwtserver.dto.LoginRequestDto;

public interface MemberService {

  void join(MemberDto memberDto) throws Exception;
  String login(LoginRequestDto loginRequestDto);
  MemberDto getMemberDtoById(String userId);
}
