package com.example.jwtserver.config;

import com.example.jwtserver.filter.CustomUserDetailsService;
import com.example.jwtserver.filter.JwtAuthFilter;
import com.example.jwtserver.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security 설정에 대한 설정 클래스
 */
@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
	private final CustomUserDetailsService customUserDetailsService;
	private final JwtUtil jwtUtil;

	/**
	 * cors 설정
	 *
	 * @return 설정한 source
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() { //cors 설정
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:5050"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	/**
	 * Security Filter Chain에 대한 전반적인 설정
	 *
	 * @param httpSecurity : security 설정 클래스
	 * @return securityFilterChain
	 * @throws Exception
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		//csrf disable
		httpSecurity.csrf((csrf) -> csrf.disable());
		//cors config 값 받아옴
		httpSecurity.cors((cors) -> cors.configurationSource(corsConfigurationSource()));

		//기본 security login 연결 disable
		httpSecurity.formLogin((form) -> form.disable());

		//인증 전 JwtAuthFilter 실행
		httpSecurity.addFilterBefore(new JwtAuthFilter(customUserDetailsService, jwtUtil),
			UsernamePasswordAuthenticationFilter.class);

		/** 인가(Roles)에 관한 처리가 필요하다면
		 httpSecurity.exceptionHandling((exceptionHandling) -> exceptionHandling
		 .authenticationEntryPoint(jwtAuthenticationEntryPoint)
		 .accessDeniedHandler(jwtAccessDeniedHandler));
		 */

		//login과 join은 permit 허용
		httpSecurity.authorizeHttpRequests((authorizeRequests) -> {
			authorizeRequests.requestMatchers("/login", "/join").permitAll();
			authorizeRequests.anyRequest().authenticated();
		});

		return httpSecurity.build();
	}

}
