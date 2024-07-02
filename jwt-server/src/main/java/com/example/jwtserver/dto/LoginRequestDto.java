package com.example.jwtserver.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
  private String id;
  private String pw;
}
