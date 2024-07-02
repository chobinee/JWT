package com.example.jwtserver.util;
import com.example.jwtserver.dto.MemberDto;
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

  public static String generateJwtToken(MemberDto memberDto)
  {
    JwtBuilder builder = Jwts.builder()
        .setHeader(createHeader())
        .setClaims(createClaims(memberDto))
        .setSubject(String.valueOf(memberDto.getId()))
        .setIssuer("profile")
        .signWith(key, SignatureAlgorithm.HS256)
        .setExpiration(createExpiredDate());

    return builder.compact();
  }

  // token 유효성 체크
  public static boolean isValidToken(String token)
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

  // token 만료 시간 설정
  private static Date createExpiredDate()
  {
    Instant now = Instant.now();
    Instant expiryDate = now.plus(Duration.ofMinutes(5));
    return Date.from(expiryDate);
  }

  // jwt header 생성
  private static Map<String, Object> createHeader()
  {
    Map<String, Object> header = new HashMap<>();

    header.put("typ", JWT_TYPE);
    header.put("alg", ALGORITHM);
    header.put("regDate", System.currentTimeMillis());

    return header;
  }

  // jwt claim 생성
  private static Map<String, Object> createClaims(MemberDto memberDto)
  {
    Map<String, Object> claims = new HashMap<>();

    log.info("id : " + memberDto.getId());
    log.info("name : " + memberDto.getName());

    claims.put(USER_ID, memberDto.getId());
    claims.put(USER_NAME, memberDto.getName());

    return claims;
  }

  // token 복호화 후 claims 반환
  private static Claims getClaimsFromToken(String token)
  {
    return Jwts.parserBuilder().setSigningKey(key)
        .build().parseClaimsJws(token).getBody();
  }

  public static String getUserIdFromToken(String token)
  {
    Claims claims = getClaimsFromToken(token);
    return claims.get(USER_ID).toString();
  }
}
