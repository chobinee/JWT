package com.example.jwtserver.service;

import com.example.jwtserver.dto.MemberDto;
import com.example.jwtserver.dto.LoginRequestDto;
import com.example.jwtserver.repository.MemberRepository;
import com.example.jwtserver.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;

  @Override
  public String join(MemberDto memberDto) {
    Member member = Member.builder()
        .id(memberDto.getId())
        .pw(memberDto.getPw())
        .name(memberDto.getName())
        .admin(memberDto.isAdmin())
        .build();

    memberRepository.save(member);

    return "success";
  }

  @Override
  public String login(LoginRequestDto loginRequestDto) {
    Member member = memberRepository.findMemberById(loginRequestDto.getId());
    if (member == null) return "404";
    if (!member.getPw().equals(loginRequestDto.getPw())) return "400";
    return "success";
  }
}
