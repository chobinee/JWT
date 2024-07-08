package com.example.jwtserver.config;

import com.example.jwtserver.filter.CustomUserDetailsService;
import com.example.jwtserver.filter.JwtAccessDeniedHandler;
import com.example.jwtserver.filter.JwtAuthFilter;
import com.example.jwtserver.filter.JwtAuthenticationEntryPoint;
import com.example.jwtserver.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.Arrays;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
  private final CustomUserDetailsService customUserDetailsService;
  private final JwtUtil jwtUtil;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:5050")); // 허용할 출처
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE")); // 허용할 HTTP 메서드
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); // 허용할 헤더
    configuration.setAllowCredentials(true); // 쿠키 허용 여부

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 설정 적용
    return source;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.csrf((csrf) -> csrf.disable());
    httpSecurity.cors(Customizer.withDefaults());
    httpSecurity.cors((cors) -> cors.configurationSource(corsConfigurationSource()));

    httpSecurity.formLogin((form) -> form.disable());
    httpSecurity.httpBasic(AbstractHttpConfigurer::disable);

    httpSecurity.addFilterBefore(new JwtAuthFilter(customUserDetailsService, jwtUtil),
        UsernamePasswordAuthenticationFilter.class);

    httpSecurity.exceptionHandling((exceptionHandling) -> exceptionHandling
        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
        .accessDeniedHandler(jwtAccessDeniedHandler));

    httpSecurity.authorizeHttpRequests((authorizeRequests) -> {
      authorizeRequests.requestMatchers("/login", "/join").permitAll();
      authorizeRequests.anyRequest().authenticated();
    });
    return httpSecurity.build();
  }

}
