package com.example.jwtserver.util;
import com.example.jwtserver.dto.MemberDto;
import com.example.jwtserver.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtil {
  private static final String secretKey = "pnpSecureWebPlatformTeam2IsGoodAndCoolAndAwesome";
  private static final Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
  private static final String JWT_TYPE = "JWT";
  private static final String ALGORITHM = "HS256";
  private static final String USER_ID = "userId";
  private static final String USER_NAME = "userName";
  private static final String USER_PW = "userPassword";
  private static final String IS_ADMIN= "isAdmin";

  public String generateAccessToken(MemberDto memberDto)
  {
    JwtBuilder builder = Jwts.builder()
        .setHeader(createHeader())
        .setClaims(createClaims(memberDto))
        .setSubject(String.valueOf(memberDto.getId()))
        .setIssuer("profile")
        .signWith(key, SignatureAlgorithm.HS256)
        .setExpiration(createAccessTokenExpiredDate());

    return builder.compact();
  }

  public String generateRefreshToken(MemberDto memberDto)
  {
    JwtBuilder builder = Jwts.builder()
        .setHeader(createHeader())
        .setClaims(createClaims(memberDto))
        .setSubject(String.valueOf(memberDto.getId()))
        .setIssuer("profile")
        .signWith(key, SignatureAlgorithm.HS256)
        .setExpiration(createRefreshTokenExpiredDate());

    return builder.compact();
  }

  // token 유효성 체크
  public boolean isValidToken(String token)
  {
    try {
      Claims claims = getClaimsFromToken(token);

      log.info("expireTime : " + claims.getExpiration());
      log.info(USER_ID + " : " + claims.get(USER_ID));
      log.info(USER_NAME + " : " + claims.get(USER_NAME));

      return true;
    } catch (ExpiredJwtException expiredJwtException) {
      log.error("Token Expired", expiredJwtException);
      return false;
    } catch (JwtException jwtException) {
      log.error("Token Tampered", jwtException);
      return false;
    } catch (NullPointerException nullPointerException) {
      log.error("Token is null", nullPointerException);
      return false;
    }
  }

  // accessToken 만료 시간 설정
  private Date createAccessTokenExpiredDate()
  {
    Instant now = Instant.now();
    Instant expiryDate = now.plus(Duration.ofSeconds(5));
    return Date.from(expiryDate);
  }

  // refreshToken 만료 시간 설정
  private Date createRefreshTokenExpiredDate()
  {
    Instant now = Instant.now();
    Instant expiryDate = now.plus(Duration.ofSeconds(10));
    return Date.from(expiryDate);
  }

  // jwt header 생성
  private Map<String, Object> createHeader()
  {
    Map<String, Object> header = new HashMap<>();

    header.put("typ", JWT_TYPE);
    header.put("alg", ALGORITHM);
    header.put("regDate", System.currentTimeMillis());

    return header;
  }

  // jwt claim 생성
  private Map<String, Object> createClaims(MemberDto memberDto)
  {
    Map<String, Object> claims = new HashMap<>();

    claims.put(USER_ID, memberDto.getId());
    claims.put(USER_NAME, memberDto.getName());
    claims.put(USER_PW, memberDto.getPw());
    claims.put(IS_ADMIN, memberDto.isAdmin());

    return claims;
  }

  // token 복호화 후 claims 반환
  private Claims getClaimsFromToken(String token)
  {
    return Jwts.parserBuilder().setSigningKey(key)
        .build().parseClaimsJws(token).getBody();
  }

  public String getUserIdFromToken(String token)
  {
    Claims claims = getClaimsFromToken(token);
    return claims.get(USER_ID).toString();
  }

  public MemberDto getMemberDtoFromToken(String token)
  {
    Claims claims = getClaimsFromToken(token);

    MemberDto memberDto = new MemberDto();
    memberDto.setId(claims.get(USER_ID).toString());
    memberDto.setName(claims.get(USER_NAME).toString());
    memberDto.setPw(claims.get(USER_PW).toString());
    memberDto.setAdmin(claims.get(IS_ADMIN).toString().equals("true")?true:false);
    return memberDto;
  }
}
