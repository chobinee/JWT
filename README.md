# JWT 

> JWT(Json Web Token)에 대해 알아보자.

## 정의
- 정보를 **_JSON_** 개체로 안전하게 전송하기 위한, 간결하고 독립적인 방법을 정의하는 개방형 표준
- 선택적 서명 및 선택적 암호화 사용
- 비밀(**HMAC**) 혹은 공개/개인 키(**RSA/ECDSA**) 쌍 알고리즘을 사용하여 서명

> JSON(JavaScript Object Notation) : 속성-값 쌍 / 시리얼화 가능한 값 쌍으로 이루어진 배열 자료형으로 인터넷에서 자료를 주고 받을 때 변수 값을 표현하는 데 사용
> 
> HMAC(Hash-based Message Authentication Code) : 메시지 인증 코드를 생성하기 위한 알고리즘으로,
> 송수신자만 공유하고 있는 키와 원본 메시지를 혼합하여 해시값을 만들고 이를 비교함으로써 무결성과 진본 확인을 동시 수행
>
> RSA : SSL, TLS 등에 사용되는 공개키 암호화 알고리즘으로, 공개키와 개인키가 한 쌍을 이루며 
> 공개키로 암호화한 내용은 개인키로만, 개인키로 암호화한 내용은 공개키로만 해독 가능

## 사용
> JWT는 언제 사용될까?
### Authorization
- 사용자가 로그인한 뒤, JWT가 포함되어 해당 토큰으로 허용되는 경로, 서비스, 리소스에 액세스 가능
### 정보 교환
- 서명을 통해 보낸 사람이 누구인지 확인 가능하며 헤더와 페이로드를 사용해 서명을 계산하므로 변조 여부 확인 가능

## 구조
* 점(.)으로 구분된 세 부분으로 구성
  - header
  - payload
  - sign
- 세 부분은 Base64url 인코딩을 사용하여 별도로 인코딩됨

일반적으로 다음과 같음
> xxxxx.yyyyy.zzzzz

code ver.
```json
const token = base64urlEncoding(header) + '.' + base64urlEncoding(payload) + '.' + base64urlEncoding(signature)
```

### Header
- 사용되는 서명 알고리즘과 토큰 유형의 두 부분으로 구성
```json
    {
        "alg" : "HS256", //HMAC-SHA256을 사용하여 서명됨
        "typ" : "JWT"
    }
```
>
> SHA(Secure Hash Algorithm) : 암호화 해시 알고리즘으로, 예측된 해시값에 대해 계산된 해시를 비교함으로써 무결성을 파악
>

### payload

### Sign


참고 링크

[jwt.io](https://jwt.io/introduction)

[wikipedia](https://ko.wikipedia.org/wiki/JSON_%EC%9B%B9_%ED%86%A0%ED%81%B0#cite_note-rfc7519-1)