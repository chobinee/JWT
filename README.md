# JWT

> JWT(Json Web Token)에 대해 알아보자.
>

# 정의

- 정보를 ***JSON*** 개체로 안전하게 전송하기 위한, 간결하고 독립적인 방법을 정의하는 개방형 표준
- 선택적 서명 및 선택적 암호화 사용
- 비밀(**HMAC**) 혹은 공개/개인 키(**RSA/ECDSA**) 쌍 알고리즘을 사용하여 서명

> JSON(JavaScript Object Notation) : 속성-값 쌍 / 시리얼화 가능한 값 쌍으로 이루어진 배열 자료형으로 인터넷에서 자료를 주고 받을 때 변수 값을 표현하는 데 사용
>
>
> HMAC(Hash-based Message Authentication Code) : 메시지 인증 코드를 생성하기 위한 알고리즘으로, 송수신자만 공유하고 있는 키와 원본 메시지를 혼합하여 해시값을 만들고 이를 비교함으로써 무결성과 진본 확인을 동시 수행
>
> RSA : SSL, TLS 등에 사용되는 공개키 암호화 알고리즘으로, 공개키와 개인키가 한 쌍을 이루며
> 공개키로 암호화한 내용은 개인키로만, 개인키로 암호화한 내용은 공개키로만 해독 가능
>

# 용도

> JWT는 언제 사용될까?
>

## Authorization

- 사용자가 로그인한 뒤, JWT가 포함되어 해당 토큰으로 허용되는 경로, 서비스, 리소스에 액세스 가능

## 정보 교환

- 서명을 통해 보낸 사람이 누구인지 확인 가능하며 헤더와 페이로드를 사용해 서명을 계산하므로 변조 여부 확인 가능

# 구조

- 점(.)으로 구분된 세 부분으로 구성
  - header
  - payload
  - signature
- 세 부분은 Base64url 인코딩을 사용하여 별도로 인코딩됨

일반적으로 다음과 같음

> xxxxx.yyyyy.zzzzz
>
- code ver.

```json
const token = base64urlEncoding(header) + '.' + base64urlEncoding(payload) + '.' + base64urlEncoding(signature)
```

## Header

- 사용되는 서명 알고리즘과 토큰 유형의 두 부분으로 구성

```json
    {
        "alg" : "HS256", //HMAC-SHA256을 사용하여 서명됨
        "typ" : "JWT"
    }

```

> SHA(Secure Hash Algorithm) : 암호화 해시 알고리즘으로, 예측된 해시값에 대해 계산된 해시를 비교함으로써 무결성을 파악
>

## payload

- 대상(사용자)에 대한 정보로 name-value 형식
- claim : 데이터 각각의 key
  - registered
  - public
  - private

### registered claim

- **세 글자**로 정의하며 필수는 아니지만 권장된 클레임 집합
  - iss(issuer) : 토큰 발급자
  - exp(expiration time) : 토큰 만료 시간
  - sub(subject) : 토큰 제목
  - aud(audience) : 토큰 대상자
  - nbf(not before) : 토큰 활성 날짜
  - iat(issued at) : 토큰 발급 시간
  - jti(jwt id) : 토큰의 고유 식별자(issuer 여러 명일 때 사용)

```jsx
{
	"sub" : "1234567890",
	"name" : "Subin Cho",
	"iat" : 1572312351
}
```

### public claim

- 사용자가 자유롭게 정의할 수 있는 클레임
- 충돌 방지를 위해 **IANA JSON Web Token Claims Registry**에 정의된 이름을 사용하거나 URI로 정의해야 함
  - URI로 정의하는 경우

    ```jsx
    {
    	"https://uriuri.com/hello" : true
    }
    ```


### private claim

- 등록되지 않은 비공개 클레임으로, 정보 공유를 위해 커스터마이징된 클레임
- 클라이언트 ↔ 서버 협의하에 사용

```jsx
{
	"tokenType" : "accessToken",
	"username" : "sbcho",
	"usage" : "access"
}
```

<aside>
💡 payload는 서명된 파트가 아니라 단순 Base64 인코딩 된 파트이기 때문에 누구나 디코딩하여 데이터 열람이 가능함. 데이터 담을 때 주의!

</aside>

## Signature

- 메시지의 **무결성 확인**에 사용
- 개인 키로 서명된 경우 송신자 확인 가능
1. Base64로 인코딩 된 header와 payload를 `.` 으로 연결
2. 비밀 키를 이용하여 header에 정의된 암호화 알고리즘으로 해싱
3. 해싱된 값을 다시 Base64로 인코딩

- HMAC SHA256 알고리즘으로 서명 생성할 경우

```jsx
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret)
```

- JWT 예시

```jsx
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IlN1YmluIENobyIsImlhdCI6MTU3MjMxMjM1MX0.u3T6xovikuX2wSi6yXg6c4x4y_y88A5qer4ga3fEm_M
```

# 유효성 검증

> 서버에서는 어떻게 토큰을 검증할까?
>
1. 받은 JWT를 Header, Payload, Signature로 분리
2. Header에서 지정된 암호화 알고리즘과 서버에 저장된 비밀 키를 사용하여 header와 payload 해싱
3. 해싱된 값과 JWT에서 추출한 signature 비교
  - 일치하면 검증 성공, 일치하지 않으면 검증 실패

# Work

- 사용자가 성공적으로 로그인하면 JWT가 반환됨
- 자격 증명이므로 필요한 만큼만 보관해서 사용
- 일반적으로 Bearer schema를 사용하여 Authorization header에 JWT를 보내서 사용

    ```jsx
    Authorization: Bearer <token>
    ```


<aside>
💡 유효 기간이 다른 JWT 2개(Access Token, Refresh Token)를 발급

Access Token : header에 담겨 리소스 접근 가능, 만료 시간이 짧음
Refresh Token : access token 만료 시 새로운 access token 발급

* refresh token 만료 시 재로그인하여 재발급!

</aside>

# 장단점

## 장점

- 데이터 위변조 방지
- 인증에 필요한 모든 정보를 가지고 있기 때문에 별도 저장소가 필요하지 않음
- 세션과 다르게 서버는 stateless가 됨 → 서버 부하 낮아짐
- 확장성이 우수함
- 모바일에서도 잘 동작함

## 단점

- 쿠키/세션과 다르게 토큰의 길이가 길어, 인증 요청이 많아질 수록 네트워크 부하도 심해짐
- payload 자체 암호화가 되지 않기 때문에 민감한 정보 담을 수 없음
- 토큰을 탈취당했을 시의 대처가 어려움

참고 자료

[jwt.io](https://jwt.io/introduction)

[wikipedia](https://ko.wikipedia.org/wiki/JSON_%EC%9B%B9_%ED%86%A0%ED%81%B0#cite_note-rfc7519-1)

https://xxeol.tistory.com/34

[https://velog.io/@chuu1019/Access-Token과-Refresh-Token이란-무엇이고-왜-필요할까](https://velog.io/@chuu1019/Access-Token%EA%B3%BC-Refresh-Token%EC%9D%B4%EB%9E%80-%EB%AC%B4%EC%97%87%EC%9D%B4%EA%B3%A0-%EC%99%9C-%ED%95%84%EC%9A%94%ED%95%A0%EA%B9%8C)

[https://velog.io/@gimminjae/JWT란-왜-사용할까-장단점은](https://velog.io/@gimminjae/JWT%EB%9E%80-%EC%99%9C-%EC%82%AC%EC%9A%A9%ED%95%A0%EA%B9%8C-%EC%9E%A5%EB%8B%A8%EC%A0%90%EC%9D%80)