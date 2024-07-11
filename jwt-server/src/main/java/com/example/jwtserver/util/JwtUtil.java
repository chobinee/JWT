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

	/**
	 * token 발급
	 *
	 * @param memberDto
	 * @param type
	 * @return
	 */
	public String generateToken(MemberDto memberDto, String type) throws IllegalArgumentException {
		Date expiredDate = null;

		if (memberDto == null || type == null) {
		    //인자가 null 값으로 들어왔을 경우
			throw new IllegalArgumentException("memberDto and type are required");

		}
		if ("access".equals(type)) {
		    //accessToken일 경우
			expiredDate = createTokenExpiredDate(Duration.ofSeconds(10));

		} else if ("refresh".equals(type)) {
			//refreshToken일 경우
			expiredDate = createTokenExpiredDate(Duration.ofSeconds(300));

		} else {
			//다른 type일 경우
			throw new IllegalArgumentException("type is not valid");

		}

		//token build
		JwtBuilder builder = Jwts.builder()
			.setHeader(createHeader())
			.setClaims(createClaims(memberDto))
			.setSubject(String.valueOf(memberDto.getId()))
			.setIssuer("profile")
			.signWith(key, SignatureAlgorithm.HS256)
			.setExpiration(expiredDate);

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
		Claims claims = getClaimsFromToken(token);

		if (claims == null) {
		    // claims가 null일 경우 return null
			return false;

		}
		return true;
	}

	/**
	 * accessToken 만료 시간 설정
	 *
	 * @param duration : 현재 시각에 더해질 만료 시간
	 * @return 만료 시간
	 */
	private Date createTokenExpiredDate(Duration duration) {
		Instant now = Instant.now();
		Instant expiryDate = now.plus(duration);
		return Date.from(expiryDate);
	}

	/**
	 * jwt header 생성
	 *
	 * @return 생성한 header
	 */
	private Map<String, Object> createHeader() {
		Map<String, Object> header = new HashMap<>();

		header.put("typ", JWT_TYPE);
		header.put("alg", ALGORITHM);
		header.put("regDate", System.currentTimeMillis());

		return header;
	}

	/**
	 * user 정보를 담은 claims 생성
	 *
	 * @param memberDto
	 * @return 생성된 claims
	 */
	private Map<String, Object> createClaims(MemberDto memberDto) {
		Map<String, Object> claims = new HashMap<>();

		claims.put(USER_ID, memberDto.getId());
		claims.put(USER_NAME, memberDto.getName());
		claims.put(USER_PW, memberDto.getPw());
		claims.put(IS_ADMIN, memberDto.isAdmin());

		return claims;
	}

	/**
	 * token 복호화 후 claim 반환
	 *
	 * @param token
	 * @return Claims, null
	 */
	private Claims getClaimsFromToken(String token) {
		try {
            //token으로부터  claims 받아옴
			Claims claims = Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();

			return claims;
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

	/**
	 * token에 있는 claim 정보로 userId 가져옴
	 *
	 * @param token
	 * @return claims, null
	 */
	public String getUserIdFromToken(String token) {
		Claims claims = getClaimsFromToken(token);
		if (claims == null) {
		    // claims가 null일 경우 return null
			return null;

		}
		return claims.get(USER_ID).toString();
	}

	/**
	 * token에 있는 claim 정보로 memberDto 가져옴
	 *
	 * @param token
	 * @return memberDto, null
	 */
	public MemberDto getMemberDtoFromToken(String token) {
		Claims claims = getClaimsFromToken(token);
		if (claims == null) {
		    // claims가 null일 경우 return null
			return null;

		}
		//memberDto build
		MemberDto memberDto = MemberDto.builder()
			.id(claims.get(USER_ID).toString())
			.pw(claims.get(USER_PW).toString())
			.name(claims.get(USER_NAME).toString())
			.admin(claims.get(IS_ADMIN).toString().equals("true"))
			.build();

		return memberDto;
	}
}
