package com.example.jwtserver.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Member entity
 */
@Entity //todo. setter 있나,,,?
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
public class Member {
	@Id
	private String id;
	private String pw;
	private String name;
	//@JsonIgnore //json 요청 보낼 때 제외 가능
	private boolean admin;
}
