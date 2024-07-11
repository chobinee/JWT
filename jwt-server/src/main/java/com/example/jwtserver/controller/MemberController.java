package com.example.jwtserver.controller;

import com.example.jwtserver.dto.MemberDto;
import com.example.jwtserver.dto.LoginRequestDto;
import com.example.jwtserver.service.MemberService;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Member Controller
 */
@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	/**
	 * 회원가입 Controller
	 *
	 * @param memberDto
	 * @return ResponseEntity
	 */
	@PostMapping("/join")
	public ResponseEntity join(@RequestBody MemberDto memberDto) {
		try {
			// join
			memberService.join(memberDto);
			return ResponseEntity.status(HttpStatus.CREATED).body("회원 가입 성공");

			// join 실패 시
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 가입 실패");

		}
	}

	/**
	 * 로그인 Controller
	 *
	 * @param loginRequestDto     : 사용자 입력 body에서 받아오는 loginRequest 정보
	 * @param httpServletResponse : 쿠키 생성을 위해 필요
	 * @return ResponseEntity
	 */
	@PostMapping("/login")
	public ResponseEntity login(
		@RequestBody LoginRequestDto loginRequestDto
		, HttpServletResponse httpServletResponse
	) {
		try {
			// login
			String token = memberService.login(loginRequestDto, httpServletResponse);

			//정상일 경우
			return ResponseEntity.status(HttpStatus.OK).body(token);
		} catch (EntityNotFoundException EntityNotFoundException) {
			// member를 못 찾았을 경우 204
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

		} catch (Exception exception) {
			//그 외 에러
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		}
	}

	/**
	 * JWT Token 검증을 위한 api
	 * JwtAuthFilter 거친 후 정상적인 상황에서만 접근 가능
	 *
	 * @return ResponseEntity
	 */
	@GetMapping("/home")
	public ResponseEntity<MemberDto> home() {
		try {
			//인증자 정보(권한 포함) 받아옴
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			//인증자 정보를 못 받아오거나 인증 실패 시
			if (authentication == null || !authentication.isAuthenticated()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

			}

			//인증자 정보에서 userDetail 받아옴
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String userId = userDetails.getUsername();

			//userId로 MemberDto 받아옴
			MemberDto memberDto = memberService.getMemberDtoById(userId);
			//정상적일 경우 ok
			return ResponseEntity.status(HttpStatus.OK).body(memberDto);

			//memberDto 못 받아왔을 경우 catch
		} catch (Exception e) {
			//이미 인증이 성공했는데 member 못 받아왔다면 Server error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		}
	}
}
