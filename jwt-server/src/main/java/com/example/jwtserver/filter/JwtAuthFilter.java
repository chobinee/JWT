package com.example.jwtserver.filter;

import com.example.jwtserver.dto.MemberDto;
import com.example.jwtserver.util.JwtUtil;
import com.example.jwtserver.util.EnumResultCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 리소스 접근 전 jwtToken 검증하는 filter
 * OncePerRequestFilter : 해당 요청에 대해 한 번만 사용
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
	private final CustomUserDetailsService customUserDetailsService;
	private final JwtUtil jwtUtil;

	/**
	 * jwt filter, 토큰 검증
	 * @param request : HTTP request
	 * @param response : HTTP response
	 * @param filterChain
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	protected void doFilterInternal(
		HttpServletRequest request
		, HttpServletResponse response
		, FilterChain filterChain
	) throws ServletException, IOException {
		String authorizationHeader = request.getHeader("Authorization");
		Cookie[] cookies = request.getCookies();

		String servletPath = request.getServletPath();

		if ("/login".equals(servletPath)) {
		    // endpoint가 login일 경우 다음 필터로 pass
			log.info("Skipping JWT filter for /login path");
			filterChain.doFilter(request, response);
			return;

		}

		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
		    //authorizationHeader 없거나 Bearer로 시작하지 않는 경우 throw
			throw new ServletException("No JWT token found");

		}

		String accessToken = authorizationHeader.substring(7);
		if (jwtUtil.isValidToken(accessToken)) {
		    // JWT 유효성 검증
			//accessToken에서 userId 가져옴
			String userId = jwtUtil.getUserIdFromToken(accessToken);
			if (userId == null) {
				throw new ServletException("Invalid JWT token");

			}

			// 유저와 토큰 일치 시 userDetails 생성
			UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);
			if (userDetails == null) {
				throw new ServletException("User not found");

			}
			// UserDetails, Password, Role -> 접근권한 인증 Token 생성
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
				new UsernamePasswordAuthenticationToken(
					userDetails
					, null
					, userDetails.getAuthorities()
				);

			// 현재 Request의 Security Context에 접근권한 설정
			SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

		} else {
			// accessToken 유효하지 않을 경우 refreshToken 유효성 검증
			String refreshToken = Arrays
				.stream(cookies)
				.filter((cookie) -> cookie.getName().equals("refreshToken"))
				.findFirst().orElseThrow(() -> new ServletException("Refresh Token not found"))
				.getValue();

			if (jwtUtil.isValidToken(refreshToken)) {
			    // refreshToken이 유효하다면
				try {
					//token으로 부터 memberDto 받아옴
					MemberDto memberDto = jwtUtil.getMemberDtoFromToken(refreshToken);
					if (memberDto == null) {
						throw new ServletException("Invalid JWT token");

					}
					//새로운 accessToken 발급
					String newAccessToken = jwtUtil.generateToken(memberDto, "access");

					if (newAccessToken == null) {
					    //accessToken 발급에 문제가 있다면 throw
						throw new ServletException("Invalid JWT Creation");

					}

					//response setting
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");

					//jsonObject 생성 (EXPIRED_ACC_TOKEN)
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("resultCode", EnumResultCode.EXPIRED_ACC_TOKEN.getCode());
					jsonObject.put("accessToken", newAccessToken);

					//response에 반영
					response.getWriter().write(jsonObject.toString());
					response.getWriter().flush();

				} catch (IllegalArgumentException illegalArgumentException) {
					// token 생성의 인자가 잘못됐을 경우
					throw new ServletException("Illegal Argument Error");

				}
			}
			else {
			    // refreshToken이 유효하지 않으면
				//response setting
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");

				//jsonObject 생성 (EXPIRED_REF_TOKEN)
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("resultCode", EnumResultCode.EXPIRED_REF_TOKEN.getCode());

				//response에 반영
				response.getWriter().write(jsonObject.toString());
				response.getWriter().flush();

			}
			return;

		}
        // 다음 필터로 넘기기
		filterChain.doFilter(request, response);

	}
}
