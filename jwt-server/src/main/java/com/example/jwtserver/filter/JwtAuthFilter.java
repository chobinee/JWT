package com.example.jwtserver.filter;

import com.example.jwtserver.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
  private final CustomUserDetailsService customUserDetailsService;
  private final JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String authorizationHeader = request.getHeader("Authorization");
    Cookie[] cookies = request.getCookies();

    String servletPath = request.getServletPath();
    logger.info("Processing request for path: {}", servletPath);
    if ("/login".equals(servletPath)) {
      logger.info("Skipping JWT filter for /login path");
      filterChain.doFilter(request, response);
      return;
    }

    // JWT가 헤더에 있는 경우
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      String accessToken = authorizationHeader.substring(7);
      // JWT 유효성 검증
      if (jwtUtil.isValidToken(accessToken)) {
        String userId = jwtUtil.getUserIdFromToken(accessToken);

        // 유저와 토큰 일치 시 userDetails 생성
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);

        if (userDetails != null) {
          // UserDetails, Password, Role -> 접근권한 인증 Token 생성
          UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
              new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

          // 현재 Request의 Security Context에 접근권한 설정
          SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
      }
      else {
        // accessToken 유효하지 않을 경우 refreshToken 유효성 검증
        String refreshToken = Arrays.stream(cookies).filter((cookie) -> cookie.getName().equals("refreshToken"))
                .findFirst().orElseThrow(() -> new ServletException("Refresh Token not found")).getValue();

        if (jwtUtil.isValidToken(refreshToken)) {
          String userId = jwtUtil.getUserIdFromToken(refreshToken);

          // 새로운 accessToken 발급 요청 및 접근 튕김

        }
      }
    }

    filterChain.doFilter(request, response); // 다음 필터로 넘기기
  }
}
