package com.example.jwtserver.filter;

import com.example.jwtserver.dto.MemberDto;

import java.util.Collection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * UserDetails interface 상속
 * spring security에서 사용하는 사용자 정보를 정의함
 */
@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

	private final MemberDto member;
	private final Collection<GrantedAuthority> authorities;

	/**
	 * 권한들 받아옴
	 *
	 * @return
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return member.getPw();
	}

	@Override
	public String getUsername() {
		return member.getId();
	}

	public String getName() {
		return member.getName();
	}

	public boolean isAdmin() {
		return member.isAdmin();
	}


	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}


}