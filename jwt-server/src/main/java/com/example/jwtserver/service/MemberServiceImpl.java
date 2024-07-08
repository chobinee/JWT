package com.example.jwtserver.service;

import com.example.jwtserver.dto.MemberDto;
import com.example.jwtserver.dto.LoginRequestDto;
import com.example.jwtserver.repository.MemberRepository;
import com.example.jwtserver.entity.Member;
import com.example.jwtserver.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;
  private final JwtUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;
  private static final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

  @Override
  public void join(MemberDto memberDto) throws Exception {
      logger.info("join start");
    if (memberRepository.existsById(memberDto.getId())) throw new Exception();
    Member member = Member.builder()
        .id(memberDto.getId())
        .pw(passwordEncoder.encode(memberDto.getPw()))
        .name(memberDto.getName())
        .admin(memberDto.isAdmin())
        .build();

    logger.info("join end");
    logger.info("member's admin : " + memberDto.isAdmin());

    memberRepository.save(member);
  }

  @Override
  public Map<String, String> login(LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse) {
    Member member = memberRepository.findMemberById(loginRequestDto.getId());
    Map<String, String> map = new HashMap<>();

    if (member == null)
    {
        map.put("code", "204");
        return map;
    }

    if (!passwordEncoder.matches(loginRequestDto.getPw(), member.getPw()))
    {
        map.put("code", "401");
        return map;
    }

    MemberDto memberDto = new MemberDto();
    memberDto.setId(member.getId());
    memberDto.setPw(member.getPw());
    memberDto.setName(member.getName());
    memberDto.setAdmin(member.isAdmin());

    String accessToken = jwtUtil.generateAccessToken(memberDto);
    String refreshToken = jwtUtil.generateRefreshToken(memberDto);

    Cookie cookie = new Cookie("refreshToken", refreshToken);
    cookie.setHttpOnly(true);
    httpServletResponse.addCookie(cookie);

    map.put("accessToken", accessToken);
    map.put("refreshToken", refreshToken);

    return map;
  }

  @Override
  public MemberDto getMemberDtoById(String userId)
  {
      Member member = memberRepository.findMemberById(userId);
      if (member == null) return null;

      MemberDto memberDto = new MemberDto();
      memberDto.setId(member.getId());
      memberDto.setPw(member.getPw());
      memberDto.setName(member.getName());
      memberDto.setAdmin(member.isAdmin());

      return memberDto;
  }
}
