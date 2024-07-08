package com.example.jwtserver.filter;

import com.example.jwtserver.dto.MemberDto;
import com.example.jwtserver.entity.Member;
import com.example.jwtserver.repository.MemberRepository;
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
import org.springframework.http.HttpStatus;
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
        logger.info("Refresh Token: {}", refreshToken);
        if (jwtUtil.isValidToken(refreshToken)) {
          MemberDto memberDto = jwtUtil.getMemberDtoFromToken(refreshToken);
          String newAccessToken = jwtUtil.generateAccessToken(memberDto);

          response.setStatus(HttpStatus.UNAUTHORIZED.value());
          response.setContentType("application/json");
          response.setCharacterEncoding("UTF-8");

          String jsonResponse = String.format("{\"resultCode\":\"%d\",\"accessToken\": \"%s\"}", 2, newAccessToken);
          response.getWriter().write(jsonResponse);
          response.getWriter().flush();

          return;
        }
        else {
          response.setStatus(HttpStatus.UNAUTHORIZED.value());
          response.setContentType("application/json");
          response.setCharacterEncoding("UTF-8");
          String jsonResponse = String.format("{\"resultCode\":\"%d\"}", 3);
          response.getWriter().write(jsonResponse);
          response.getWriter().flush();

          return;
        }
      }
    }

    filterChain.doFilter(request, response); // 다음 필터로 넘기기
  }
}
