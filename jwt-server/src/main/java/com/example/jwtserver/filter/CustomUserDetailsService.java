package com.example.jwtserver.filter;

import com.example.jwtserver.dto.MemberDto;
import com.example.jwtserver.entity.Member;
import com.example.jwtserver.repository.MemberRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
    Member member = memberRepository.findMemberById(id);
    if (member == null) throw new UsernameNotFoundException("해당하는 유저가 없습니다.");

    MemberDto memberDto = new MemberDto();
    memberDto.setId(member.getId());
    memberDto.setPw(member.getPw());
    memberDto.setName(member.getName());
    memberDto.setAdmin(member.isAdmin());

    List<GrantedAuthority> authorities = new ArrayList<>();
    if (member.isAdmin()) {
      authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
    } else {
      authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
    }

    return new CustomUserDetails(memberDto, authorities);
  }
}
