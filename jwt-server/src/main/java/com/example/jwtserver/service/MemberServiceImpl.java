package com.example.jwtserver.service;

import com.example.jwtserver.dto.MemberDto;
import com.example.jwtserver.dto.LoginRequestDto;
import com.example.jwtserver.repository.MemberRepository;
import com.example.jwtserver.entity.Member;
import com.example.jwtserver.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;
  private final JwtUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;
  private static final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

  @Override
  public void join(MemberDto memberDto) throws Exception {
    if (memberRepository.existsById(memberDto.getId())) throw new Exception();
    Member member = Member.builder()
        .id(memberDto.getId())
        .pw(passwordEncoder.encode(memberDto.getPw()))
        .name(memberDto.getName())
        .admin(memberDto.isAdmin())
        .build();

    memberRepository.save(member);
  }

  @Override
  public String login(LoginRequestDto loginRequestDto) {
    logger.info("hello");
    Member member = memberRepository.findMemberById(loginRequestDto.getId());
    if (member == null) return "404";

    if (!passwordEncoder.matches(loginRequestDto.getPw(), member.getPw())) return "400";

    MemberDto memberDto = new MemberDto();
    memberDto.setId(member.getId());
    memberDto.setPw(member.getPw());
    memberDto.setName(member.getName());
    memberDto.setAdmin(member.isAdmin());

    String accessToken = jwtUtil.generateAccessToken(memberDto);
    String refreshToken = jwtUtil.generateRefreshToken(memberDto);
    return accessToken;
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
