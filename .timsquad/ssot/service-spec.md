# Folix 서비스 명세서

**Version**: 1.0
**Created**: 2026-02-11
**Base URL**: `http://localhost:8080/api/v1`

---

## 1. 개요

### 1.1 API 규칙

- **Content-Type**: `application/json`
- **날짜 형식**: ISO 8601 (`YYYY-MM-DDTHH:mm:ssZ`)
- **금액 형식**: 문자열 (BigDecimal 정밀도 유지, e.g. `"10000.00000000"`)
- **통화 코드**: ISO 4217 (e.g. `USD`, `KRW`) + 크립토 코드 (e.g. `BTC`, `ETH`)

### 1.2 공통 응답 형식

```json
{
  "success": true,
  "data": { },
  "error": null
}
```

### 1.3 에러 응답 형식

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "ERROR_CODE",
    "message": "Human readable message"
  }
}
```

> 에러 코드 상세: [error-codes.md](./error-codes.md)

---

## 2. Assets

### 2.1 자산 등록

| 항목 | 값 |
|-----|---|
| **Endpoint** | `POST /assets` |
| **설명** | 새 자산 등록 |

#### Request

| Field | Type | Required | Description |
|-------|------|:--------:|-------------|
| symbol | string | ✅ | 종목 코드 (e.g. AAPL, BTC) |
| name | string | ✅ | 자산명 |
| type | string | ✅ | STOCK, CRYPTO, BOND, CASH, REAL_ESTATE, FUND, OTHER |
| currency | string | ✅ | 기준 통화 코드 |
| exchange | string | ❌ | 거래소 (e.g. NASDAQ, Binance) |

#### Response (201 Created)

| Field | Type | Description |
|-------|------|-------------|
| id | string | 자산 ID (UUID) |
| symbol | string | 종목 코드 |
| name | string | 자산명 |
| type | string | 자산 유형 |
| currency | string | 기준 통화 |

---

### 2.2 자산 목록 조회

| 항목 | 값 |
|-----|---|
| **Endpoint** | `GET /assets` |
| **설명** | 자산 목록 조회 |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|:--------:|-------------|
| type | string | ❌ | 자산 유형 필터 |
| currency | string | ❌ | 통화 필터 (Phase 4 예정) |

#### Response (200 OK)

```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "symbol": "AAPL",
      "name": "Apple Inc.",
      "type": "STOCK",
      "currency": "USD"
    }
  ]
}
```

---

## 3. Transactions

### 3.1 거래 기록

| 항목 | 값 |
|-----|---|
| **Endpoint** | `POST /transactions` |
| **설명** | 새 거래 기록 |

#### Request

| Field | Type | Required | Description |
|-------|------|:--------:|-------------|
| accountId | string | ✅ | 계좌 ID |
| assetId | string | ✅ | 자산 ID |
| type | string | ✅ | BUY, SELL, DIVIDEND, INTEREST, DEPOSIT, WITHDRAWAL |
| quantity | string | ✅ | 수량 (BigDecimal 문자열) |
| price | string | ✅ | 단가 (BigDecimal 문자열) |
| currency | string | ✅ | 거래 통화 |
| fee | string | ❌ | 수수료 (BigDecimal 문자열) |
| date | string | ✅ | 거래일 (ISO 8601) |
| note | string | ❌ | 메모 |

#### Response (201 Created)

| Field | Type | Description |
|-------|------|-------------|
| id | string | 거래 ID |
| accountId | string | 계좌 ID |
| assetId | string | 자산 ID |
| type | string | 거래 유형 |
| quantity | string | 수량 |
| price | string | 단가 |
| totalAmount | string | 총 금액 (수량 x 단가) |
| fee | string | 수수료 |
| date | string | 거래일 |

---

### 3.2 거래 일괄 임포트

| 항목 | 값 |
|-----|---|
| **Endpoint** | `POST /transactions/import` |
| **설명** | CSV/JSON 일괄 임포트 |

#### Request

| Field | Type | Required | Description |
|-------|------|:--------:|-------------|
| format | string | ✅ | csv, json |
| accountId | string | ✅ | 대상 계좌 ID |
| data | string | ✅ | 파일 내용 (Base64 또는 raw) |

#### Response (200 OK)

| Field | Type | Description |
|-------|------|-------------|
| imported | number | 성공 건수 |
| failed | number | 실패 건수 |
| errors | array | 실패 상세 |

---

## 4. Accounts

### 4.1 계좌 등록

| 항목 | 값 |
|-----|---|
| **Endpoint** | `POST /accounts` |
| **설명** | 새 계좌 등록 |

#### Request

| Field | Type | Required | Description |
|-------|------|:--------:|-------------|
| name | string | ✅ | 계좌명 |
| type | string | ✅ | BROKERAGE, BANK, EXCHANGE, WALLET, OTHER |
| currency | string | ✅ | 기준 통화 |
| institution | string | ❌ | 금융기관명 |

#### Response (201 Created)

| Field | Type | Description |
|-------|------|-------------|
| id | string | 계좌 ID |
| name | string | 계좌명 |
| type | string | 계좌 유형 |
| currency | string | 기준 통화 |

---

## 5. Portfolio

### 5.1 포트폴리오 요약

| 항목 | 값 |
|-----|---|
| **Endpoint** | `GET /portfolio` |
| **설명** | 포트폴리오 전체 요약 |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|:--------:|-------------|
| baseCurrency | string | ❌ | 기준 통화 (기본: USD) |

#### Response (200 OK)

| Field | Type | Description |
|-------|------|-------------|
| totalValue | string | 총 평가금액 |
| totalCost | string | 총 투자금액 |
| totalReturn | string | 총 수익금액 |
| returnRate | string | 총 수익률 (%) |
| baseCurrency | string | 기준 통화 |
| positionCount | number | 보유 종목 수 |

---

### 5.2 보유 포지션

| 항목 | 값 |
|-----|---|
| **Endpoint** | `GET /portfolio/positions` |
| **설명** | 보유 포지션 목록 |

#### Response (200 OK)

```json
{
  "success": true,
  "data": [
    {
      "assetId": "uuid",
      "symbol": "AAPL",
      "name": "Apple Inc.",
      "quantity": "10.00000000",
      "avgCost": "150.25000000",
      "currentPrice": "175.50000000",
      "marketValue": "1755.00000000",
      "unrealizedPnl": "252.50000000",
      "returnRate": "16.80",
      "weight": "25.50",
      "currency": "USD"
    }
  ]
}
```

---

### 5.3 자산 배분

| 항목 | 값 |
|-----|---|
| **Endpoint** | `GET /portfolio/allocation` |
| **설명** | 자산 배분 현황 |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|:--------:|-------------|
| groupBy | string | ❌ | type, currency, account (기본: type) |

---

### 5.4 성과 조회

| 항목 | 값 |
|-----|---|
| **Endpoint** | `GET /portfolio/performance` |
| **설명** | 포트폴리오 성과 (TWR/IRR) |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|:--------:|-------------|
| from | string | ❌ | 시작일 (ISO 8601) |
| to | string | ❌ | 종료일 (ISO 8601) |

#### Response (200 OK)

| Field | Type | Description |
|-------|------|-------------|
| twr | string | 시간가중수익률 (%) |
| irr | string | 내부수익률 (%) |
| volatility | string | 연환산 변동성 (%) |
| sharpeRatio | string | 샤프비율 |
| maxDrawdown | string | 최대낙폭 (%) |
| period | object | 조회 기간 |

---

## 6. Simulations

### 6.1 시뮬레이션 실행

| 항목 | 값 |
|-----|---|
| **Endpoint** | `POST /simulations` |
| **설명** | 몬테카를로 시뮬레이션 실행 |

#### Request

| Field | Type | Required | Description |
|-------|------|:--------:|-------------|
| type | string | ✅ | MONTE_CARLO, SCENARIO |
| iterations | number | ❌ | 시뮬레이션 횟수 (기본: 10000) |
| horizonMonths | number | ✅ | 예측 기간 (월) |
| initialAmount | string | ✅ | 초기 투자금 |
| monthlyContribution | string | ❌ | 월별 추가 투자 |

#### Response (202 Accepted)

| Field | Type | Description |
|-------|------|-------------|
| id | string | 시뮬레이션 ID |
| status | string | RUNNING |

---

### 6.2 시뮬레이션 결과 조회

| 항목 | 값 |
|-----|---|
| **Endpoint** | `GET /simulations/{id}` |
| **설명** | 시뮬레이션 결과 조회 |

#### Response (200 OK)

| Field | Type | Description |
|-------|------|-------------|
| id | string | 시뮬레이션 ID |
| status | string | COMPLETED, RUNNING, FAILED |
| percentiles | object | p5, p25, p50, p75, p95 결과 |
| timeline | array | 월별 예측 시계열 |
| metadata | object | 파라미터, 실행 시간 |

---

## 7. Net Worth

### 7.1 순자산 현황

| 항목 | 값 |
|-----|---|
| **Endpoint** | `GET /net-worth` |
| **설명** | 현재 순자산 현황 |

---

### 7.2 순자산 시계열

| 항목 | 값 |
|-----|---|
| **Endpoint** | `GET /net-worth/timeline` |
| **설명** | 순자산 시계열 데이터 |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|:--------:|-------------|
| from | string | ❌ | 시작일 |
| to | string | ❌ | 종료일 |
| interval | string | ❌ | daily, weekly, monthly |

---

## 8. Market Data

### 8.1 시세 조회

| 항목 | 값 |
|-----|---|
| **Endpoint** | `GET /prices/{symbol}` |
| **설명** | 자산 시세 조회 |

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|:--------:|-------------|
| symbol | string | ✅ | 자산 심볼 (e.g. AAPL, BTC) |

#### Response (200 OK)

| Field | Type | Description |
|-------|------|-------------|
| symbol | string | 자산 심볼 |
| price | string | 현재 시세 (BigDecimal) |
| currency | string | 통화 코드 |
| source | string | 데이터 소스 (YahooFinance, CoinGecko, DB_FALLBACK) |
| date | string | 조회 날짜 (ISO 8601) |

---

### 8.2 환율 조회

| 항목 | 값 |
|-----|---|
| **Endpoint** | `GET /exchange-rates` |
| **설명** | 환율 조회 |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|:--------:|-------------|
| from | string | ✅ | 원본 통화 |
| to | string | ✅ | 대상 통화 |
| date | string | ❌ | 특정 날짜 (기본: 최신) |

#### Response (200 OK)

| Field | Type | Description |
|-------|------|-------------|
| baseCurrency | string | 원본 통화 |
| quoteCurrency | string | 대상 통화 |
| rate | string | 환율 (BigDecimal) |
| date | string | 환율 날짜 |
| source | string | 데이터 소스 (ECB, DB_FALLBACK) |

---

## 9. Health

### 9.1 헬스체크

| 항목 | 값 |
|-----|---|
| **Endpoint** | `GET /health` |
| **설명** | 서비스 상태 확인 |

#### Response (200 OK)

```json
{
  "status": "UP",
  "components": {
    "db": "UP",
    "redis": "UP"
  }
}
```

---

## 10. 변경 이력

| 버전 | 날짜 | 변경 내용 | 작성자 |
|-----|-----|----------|-------|
| 1.0 | 2026-02-11 | 최초 작성 | ericson |
