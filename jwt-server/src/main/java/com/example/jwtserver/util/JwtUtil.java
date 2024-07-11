package com.example.jwtserver.util;

import com.example.jwtserver.dto.MemberDto;
import com.example.jwtserver.repository.MemberRepository;
import io.jsonwebtoken.*;
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

/**
 * jwtToken 생성, 조회
 */
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
	private static final String IS_ADMIN = "isAdmin";

	//token 발급
	public String generateToken(MemberDto memberDto, String type) {
		Date expiredDate = null;

		//todo. memberDto, type null 처리

		//accessToken일 경우
		if ("access".equals(type)) {
			expiredDate = createTokenExpiredDate(Duration.ofSeconds(10));

			//refreshToken일 경우
		} else if ("refresh".equals(type)) {
			expiredDate = createTokenExpiredDate(Duration.ofSeconds(300));

			//다른 type일 경우
		} else {
			// todo. throw 하되 예외 좀더 구분
			return null;

		}

		//todo. 엔터
		//token build
		JwtBuilder builder = Jwts.builder()
				.setHeader(createHeader()).setClaims(createClaims(memberDto)).setSubject(String.valueOf(memberDto.getId())).setIssuer("profile").signWith(key, SignatureAlgorithm.HS256).setExpiration(expiredDate);

		// 압축 & 서명해서 return
		return builder.compact();
	}

	/**
	 * token 유효성 체크
	 *
	 * @param token
	 * @return : 유효성(boolean)
	 */
	public boolean isValidToken(String token) {
		try {
			Claims claims = getClaimsFromToken(token);
			log.info(claims.toString());
			return true;

		} catch (ExpiredJwtException expiredJwtException) {
			log.error("Token Expired", expiredJwtException);

		} catch (UnsupportedJwtException unsupportedJwtException) {
			log.error("Token Unsupported", unsupportedJwtException);

		} catch (MalformedJwtException malformedJwtException) {
			log.error("Token is invalid", malformedJwtException);

		} catch (SignatureException signatureException) {
			log.error("Signature is invalid", signatureException);

		} catch (IllegalArgumentException illegalArgumentException) {
			log.error("Illegal argument", illegalArgumentException);

		} catch (Exception e) {
			log.error("Unexpected error", e);
		}
		return false;
	}

	// accessToken 만료 시간 설정
	private Date createTokenExpiredDate(Duration duration) {
		Instant now = Instant.now();
		Instant expiryDate = now.plus(duration);
		return Date.from(expiryDate);
	}

	// jwt header 생성
	private Map<String, Object> createHeader() {
		Map<String, Object> header = new HashMap<>();

		header.put("typ", JWT_TYPE);
		header.put("alg", ALGORITHM);
		header.put("regDate", System.currentTimeMillis());

		return header;
	}

	// jwt claim 생성
	private Map<String, Object> createClaims(MemberDto memberDto) {
		Map<String, Object> claims = new HashMap<>();

		claims.put(USER_ID, memberDto.getId());
		claims.put(USER_NAME, memberDto.getName());
		claims.put(USER_PW, memberDto.getPw());
		claims.put(IS_ADMIN, memberDto.isAdmin());

		return claims;
	}

	// token 복호화 후 claims 반환
	// todo. enter!
	private Claims getClaimsFromToken(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
		return Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	//token에 있는 claim 정보로 userId 가져옴
	public String getUserIdFromToken(String token) {
		try {
			Claims claims = getClaimsFromToken(token);
			return claims.get(USER_ID).toString();

		} catch (ExpiredJwtException expiredJwtException) {
			log.error("Token Expired", expiredJwtException);

		} catch (UnsupportedJwtException unsupportedJwtException) {
			log.error("Token Unsupported", unsupportedJwtException);

		} catch (MalformedJwtException malformedJwtException) {
			log.error("Token is invalid", malformedJwtException);

		} catch (SignatureException signatureException) {
			log.error("Signature is invalid", signatureException);

		} catch (IllegalArgumentException illegalArgumentException) {
			log.error("Illegal argument", illegalArgumentException);

		} catch (Exception e) {
			log.error("Unexpected error", e);

		}
		return null;
	}

	//token에 있는 claim 정보로 memberDto 가져옴
	public MemberDto getMemberDtoFromToken(String token) {
		try {
			Claims claims = getClaimsFromToken(token);
			MemberDto memberDto = MemberDto.builder().id(claims.get(USER_ID).toString()).pw(claims.get(USER_PW).toString()).name(claims.get(USER_NAME).toString()).admin(claims.get(IS_ADMIN).toString().equals("true")).build();

			return memberDto;

		} catch (ExpiredJwtException expiredJwtException) {
			log.error("Token Expired", expiredJwtException);

		} catch (UnsupportedJwtException unsupportedJwtException) {
			log.error("Token Unsupported", unsupportedJwtException);

		} catch (MalformedJwtException malformedJwtException) {
			log.error("Token is invalid", malformedJwtException);

		} catch (SignatureException signatureException) {
			log.error("Signature is invalid", signatureException);

		} catch (IllegalArgumentException illegalArgumentException) {
			log.error("Illegal argument", illegalArgumentException);

		} catch (Exception e) {
			log.error("Unexpected error", e);

		}
		return null;
	}
}
