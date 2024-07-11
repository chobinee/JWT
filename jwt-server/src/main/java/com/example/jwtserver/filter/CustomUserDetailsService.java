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

/**
 * UserDetailsService interface 상속
 * spring security에서 사용하는 사용자 정보를 가져옴
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	private final MemberRepository memberRepository;

	/**
	 * userId로부터 User Detail 받아옴
	 *
	 * @param id : user의 id
	 * @return UserDetail
	 * @throws UsernameNotFoundException
	 */
	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		Member member = memberRepository.findMemberById(id);
		if (member == null) {
			throw new UsernameNotFoundException("해당하는 유저가 없습니다.");
		}

		// member -> memberDto로 변환
		MemberDto memberDto = MemberDto.builder()
			.id(member.getId())
			.pw(member.getPw())
			.name(member.getName())
			.admin(member.isAdmin()).build();

		//member의 관리자 여부에 따른 Role 설정
		List<GrantedAuthority> authorities = new ArrayList<>();
		if (member.isAdmin()) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

		} else {
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

		}

		return new CustomUserDetails(memberDto, authorities);
	}
}
