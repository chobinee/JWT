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
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
  private final CustomUserDetailsService customUserDetailsService;
  private final JwtUtil jwtUtil;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.csrf((csrf) -> csrf.disable());
    httpSecurity.cors(Customizer.withDefaults());

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
