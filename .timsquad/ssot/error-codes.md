# Folix 에러 코드

**Version**: 1.0
**Created**: 2026-02-11

---

## 1. 에러 응답 형식

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "AUTH_001",
    "message": "Invalid credentials"
  }
}
```

---

## 2. HTTP 상태 코드 가이드

| HTTP | 용도 |
|------|-----|
| 400 | Bad Request - 잘못된 요청 파라미터 |
| 401 | Unauthorized - 인증 필요 또는 실패 |
| 403 | Forbidden - 권한 없음 |
| 404 | Not Found - 리소스 없음 |
| 409 | Conflict - 충돌 (중복 등) |
| 422 | Unprocessable Entity - 유효성 검증 실패 |
| 429 | Too Many Requests - Rate Limit |
| 500 | Internal Server Error - 서버 오류 |

---

## 3. 에러 코드 목록

### 3.1 AUTH (인증)

| Code | HTTP | Message | Description |
|------|------|---------|-------------|
| AUTH_001 | 401 | Invalid credentials | 잘못된 이메일 또는 비밀번호 |
| AUTH_002 | 403 | Account disabled | 비활성화된 계정 |
| AUTH_003 | 401 | Invalid token | 유효하지 않은 토큰 |
| AUTH_004 | 401 | Token expired | 만료된 토큰 |
| AUTH_005 | 401 | Token required | 토큰 누락 |

### 3.2 USER (사용자)

| Code | HTTP | Message | Description |
|------|------|---------|-------------|
| USER_001 | 404 | User not found | 사용자를 찾을 수 없음 |
| USER_002 | 409 | Email already exists | 이미 등록된 이메일 |
| USER_003 | 403 | Permission denied | 권한 없음 |

### 3.3 VALIDATION (유효성)

| Code | HTTP | Message | Description |
|------|------|---------|-------------|
| VAL_001 | 400 | Required field missing | 필수 필드 누락 |
| VAL_002 | 400 | Invalid email format | 잘못된 이메일 형식 |
| VAL_003 | 400 | Password too weak | 비밀번호 강도 부족 |
| VAL_004 | 400 | Invalid date format | 잘못된 날짜 형식 |

### 3.4 RESOURCE (리소스)

| Code | HTTP | Message | Description |
|------|------|---------|-------------|
| RES_001 | 404 | Resource not found | 리소스를 찾을 수 없음 |
| RES_002 | 409 | Resource already exists | 리소스가 이미 존재함 |
| RES_003 | 409 | Resource in use | 사용 중인 리소스 |

### 3.5 SYSTEM (시스템)

| Code | HTTP | Message | Description |
|------|------|---------|-------------|
| SYS_001 | 500 | Internal server error | 내부 서버 오류 |
| SYS_002 | 503 | Service unavailable | 서비스 이용 불가 |
| SYS_003 | 429 | Rate limit exceeded | 요청 한도 초과 |

---

## 4. 도메인별 에러 코드

### 4.1 ASSET (자산)

| Code | HTTP | Message | Description |
|------|------|---------|-------------|
| ASSET_001 | 404 | Asset not found | 자산을 찾을 수 없음 |
| ASSET_002 | 409 | Asset symbol already exists | 동일 심볼의 자산이 이미 존재 |
| ASSET_003 | 400 | Invalid asset type | 잘못된 자산 유형 |

### 4.2 ACCT (계좌)

| Code | HTTP | Message | Description |
|------|------|---------|-------------|
| ACCT_001 | 404 | Account not found | 계좌를 찾을 수 없음 |

### 4.3 TXN (거래)

| Code | HTTP | Message | Description |
|------|------|---------|-------------|
| TXN_001 | 404 | Transaction not found | 거래를 찾을 수 없음 |
| TXN_002 | 400 | Invalid transaction data | 거래 데이터가 유효하지 않음 |
| TXN_003 | 400 | Referenced account not found | 참조된 계좌가 존재하지 않음 |
| TXN_004 | 400 | Referenced asset not found | 참조된 자산이 존재하지 않음 |

### 4.4 PORT (포트폴리오)

| Code | HTTP | Message | Description |
|------|------|---------|-------------|
| PORT_001 | 400 | Invalid currency code | 잘못된 통화 코드 |
| PORT_002 | 400 | Invalid date range | 잘못된 기간 범위 |

### 4.5 MKT (시장 데이터)

| Code | HTTP | Message | Description |
|------|------|---------|-------------|
| MKT_001 | 404 | Market data not found | 시장 데이터를 찾을 수 없음 |

### 4.6 EXT (외부 연동)

| Code | HTTP | Message | Description |
|------|------|---------|-------------|
| EXT_001 | 502 | External API error | 외부 API 호출 실패 |

---

## 5. 에러 코드 네이밍 규칙

```
{DOMAIN}_{NUMBER}

- DOMAIN: 대문자, 2-6자
- NUMBER: 3자리 숫자, 001부터 시작
```

**예시:**
- `AUTH_001` - 인증 도메인 첫 번째 에러
- `ORDER_005` - 주문 도메인 다섯 번째 에러

---

## 6. 변경 이력

| 버전 | 날짜 | 변경 내용 | 작성자 |
|-----|-----|----------|-------|
| 1.0 | 2026-02-11 | 최초 작성 | |
