package com.example.jwtserver.dto;

import lombok.Data;

@Data
public class MemberDto {
  private String id;
  private String pw;
  private String name;
  private boolean admin;

}
