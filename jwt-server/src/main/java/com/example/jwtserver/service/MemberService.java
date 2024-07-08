package com.example.jwtserver.service;

import com.example.jwtserver.dto.MemberDto;
import com.example.jwtserver.dto.LoginRequestDto;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

public interface MemberService {

  void join(MemberDto memberDto) throws Exception;
  public Map<String, String> login(LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse);
  public MemberDto getMemberDtoById(String userId);
}
