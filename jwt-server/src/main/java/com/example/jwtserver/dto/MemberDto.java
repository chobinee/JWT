package com.example.jwtserver.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Member 정보를 담은 Dto,
 * Token 생성에 사용
 */
@Data
@Builder
public class MemberDto {
	private String id;
	private String pw;
	private String name;
	private boolean admin;

}
