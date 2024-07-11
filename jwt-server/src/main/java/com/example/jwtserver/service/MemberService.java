package com.example.jwtserver.service;

import com.example.jwtserver.dto.MemberDto;
import com.example.jwtserver.dto.LoginRequestDto;
import com.example.jwtserver.repository.MemberRepository;
import com.example.jwtserver.entity.Member;
import com.example.jwtserver.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 로그인, 회원가입 등 비즈니스 로직 처리
 */
@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;
	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;

	/**
	 * join logic
	 * @param memberDto
	 * @throws Exception
	 */
	public void join(MemberDto memberDto) throws Exception {
		// 이미 db에 id가 있을 경우
		if (memberRepository.existsById(memberDto.getId())) {
			throw new Exception();

		}
		//member build
		Member member = Member.builder()
			.id(memberDto.getId())
			.pw(passwordEncoder.encode(memberDto.getPw()))
			.name(memberDto.getName())
			.admin(memberDto.isAdmin())
			.build();

		//member 저장
		memberRepository.save(member);
	}

	/**
	 * login logic
	 * @param loginRequestDto
	 * @param httpServletResponse
	 * @return token
	 */
	public String login(LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse) throws Exception {
		try {
			//loginRequestDto에 있는 id로 User 조회해서 받아옴
			Member member = memberRepository.findMemberById(loginRequestDto.getId());

			//없을 시 throw
			if (member == null) {
				throw new EntityNotFoundException();

			}

			//password 틀릴 시 throw
			if (!passwordEncoder.matches(loginRequestDto.getPw(), member.getPw())) {
				throw new EntityNotFoundException();

			}

			//memberDto build
			MemberDto memberDto = MemberDto.builder()
				.id(member.getId())
				.pw(member.getPw())
				.name(member.getName())
				.admin(member.isAdmin()).build();

			//jwtToken 생성
			String accessToken = jwtUtil.generateToken(memberDto, "access");
			String refreshToken = jwtUtil.generateToken(memberDto, "refresh");

			//token null일 시
			if (accessToken == null || refreshToken == null) {
				throw new Exception("Token create error");

			}

			//cookie 생성 후 refreshToken 저장
			Cookie cookie = new Cookie("refreshToken", refreshToken);
			cookie.setHttpOnly(true);
			cookie.setSecure(false);
			cookie.setPath("/");
			httpServletResponse.addCookie(cookie);

			return accessToken;
		} catch (Exception e) {
			e.printStackTrace();

			throw new IllegalArgumentException();
		}
	}

	/**
	 * @param userId
	 * @return memberDto
	 */
	public MemberDto getMemberDtoById(String userId) throws EntityNotFoundException {
		Member member = memberRepository.findMemberById(userId);

		//member 못 받아올 경우 throw
		if (member == null) {
			throw new EntityNotFoundException();

		}

		//memberDto build
		MemberDto memberDto = MemberDto.builder()
			.id(member.getId())
			.pw(member.getPw())
			.name(member.getName())
			.admin(member.isAdmin()).build();

		return memberDto;
	}
}
