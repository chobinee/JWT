package com.example.jwtserver.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Login 요청 정보를 담은 Dto
 * 로그인 시 body에서 추출
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {
	@NotNull
	private String id;
	@NotNull
	private String pw;
}
