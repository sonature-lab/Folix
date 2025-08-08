---
title: "용어 사전 (Glossary)"
version: 1.0.0
last_updated: 2026-02-11
author: tsq-planner
status: draft
project: Folix
---

# 용어 사전 (Glossary)

> 프로젝트에서 사용하는 도메인 용어를 정의합니다.
> 모든 팀원과 에이전트가 동일한 언어로 소통하기 위한 기준 문서입니다.

---

## 1. 비즈니스 용어

| 용어 | 영문 | 정의 | 예시 | 비고 |
|-----|------|------|------|------|
| 포트폴리오 | Portfolio | 투자자가 보유한 자산의 집합 | 주식+크립토+채권 포트폴리오 | |
| 포지션 | Position | 특정 자산의 보유 상태 | AAPL 10주 보유 | 수량, 평균단가, 평가금액 포함 |
| 시간가중수익률 | TWR (Time-Weighted Return) | 현금 유출입 영향을 제거한 순수 투자 성과 | | 펀드 매니저 성과 평가 표준 |
| 내부수익률 | IRR (Internal Rate of Return) | NPV를 0으로 만드는 할인율 | | Newton-Raphson 방법으로 근사 |
| 최대낙폭 | MDD (Maximum Drawdown) | 고점 대비 최대 하락폭 (%) | 고점 100 → 저점 80 = MDD 20% | |
| 샤프비율 | Sharpe Ratio | 위험 대비 초과수익 지표 | (수익률-무위험수익률)/변동성 | |
| 변동성 | Volatility | 수익률의 표준편차 | 연환산 = 일별 × √252 | |
| 손익 | PnL (Profit and Loss) | 투자 손익 | | Unrealized/Realized 구분 |
| 순자산가치 | NAV (Net Asset Value) | 총자산 - 총부채 | | |
| 분류체계 | Taxonomy | 트리 구조 자산 분류 | 유형/지역/산업/통화별 | Portfolio Performance 참고 |
| 몬테카를로 | Monte Carlo Simulation | 확률적 시뮬레이션 | 10,000회 반복 시뮬레이션 | |
| 시나리오 | Scenario | "만약 ~했더라면" 가정 분석 | 1년 전 BTC를 샀다면? | |

---

## 2. 기술 용어

| 용어 | 영문 | 정의 | 관련 문서 |
|-----|------|------|----------|
| Clean Architecture | Clean Architecture | 의존성 방향이 도메인 안쪽으로만 향하는 아키텍처 패턴 | planning.md |
| SSOT | Single Source of Truth | 유일한 진실의 원천 (CLAUDE.md + .timsquad/ssot/) | CLAUDE.md |
| Inbound Port | Inbound Port | 외부 → 도메인 인터페이스 (유즈케이스) | planning.md |
| Outbound Port | Outbound Port | 도메인 → 외부 인터페이스 (레포지토리, API) | planning.md |
| VO | Value Object | 값의 동등성으로 비교되는 객체 | data-design.md |
| DTO | Data Transfer Object | 계층 간 데이터 전달 객체 | service-spec.md |
| DSL | Domain-Specific Language | 도메인 특화 언어 (`10_000.USD`) | requirements.md |

---

## 3. 도메인 모델

### 3.1 핵심 엔티티

| 엔티티 | 설명 | 주요 속성 | 관계 |
|-------|------|----------|------|
| Account | 투자 계좌 | id, name, type, currency | Transaction (1:N) |
| Asset | 투자 자산 | id, symbol, name, type, currency | Transaction (1:N), Classification (N:M) |
| Transaction | 거래 내역 | id, type, quantity, price, date | Account (N:1), Asset (N:1) |
| Portfolio | 포트폴리오 | positions, allocations | Account (1:N), Position (1:N) |
| TaxonomyNode | 분류 트리 노드 | id, name, type, parent | AssetClassification (1:N), self-ref |
| Simulation | 시뮬레이션 | id, type, status, parameters, result | |

### 3.2 값 객체 (Value Objects)

| 값 객체 | 설명 | 구성 요소 |
|--------|------|----------|
| Currency | 통화 | code (String) |
| Money | 금액 | amount (BigDecimal), currency (Currency) |
| Wallet | 다통화 보유 | Map<Currency, Money> |
| ExchangeRate | 환율 | from (Currency), to (Currency), rate (BigDecimal) |
| Position | 보유 포지션 | asset, quantity, avgCost, currentPrice |
| Allocation | 자산 배분 | category, weight (%), value |

---

## 4. 상태 정의

### 4.1 Simulation 상태

```
[PENDING] → [RUNNING] → [COMPLETED]
                 ↓
             [FAILED]
```

| 상태 | 코드 | 설명 | 전이 가능 상태 |
|-----|------|------|--------------|
| 대기 | PENDING | 시뮬레이션 생성됨 | RUNNING |
| 실행중 | RUNNING | 시뮬레이션 진행 중 | COMPLETED, FAILED |
| 완료 | COMPLETED | 결과 생성 완료 | - |
| 실패 | FAILED | 오류로 실패 | - |

---

## 5. 코드/열거형

### 5.1 AssetType (자산 유형)

| 코드 | 설명 |
|-----|------|
| STOCK | 주식 |
| CRYPTO | 암호화폐 |
| BOND | 채권 |
| CASH | 현금/예금 |
| REAL_ESTATE | 부동산 |
| FUND | 펀드/ETF |
| OTHER | 기타 |

### 5.2 TransactionType (거래 유형)

| 코드 | 설명 |
|-----|------|
| BUY | 매수 |
| SELL | 매도 |
| DIVIDEND | 배당 |
| INTEREST | 이자 |
| DEPOSIT | 입금 |
| WITHDRAWAL | 출금 |

### 5.3 AccountType (계좌 유형)

| 코드 | 설명 |
|-----|------|
| BROKERAGE | 증권 계좌 |
| BANK | 은행 계좌 |
| EXCHANGE | 거래소 (크립토) |
| WALLET | 지갑 (크립토) |
| OTHER | 기타 |

### 5.4 TaxonomyType (분류 축)

| 코드 | 설명 |
|-----|------|
| ASSET_TYPE | 자산 유형별 |
| REGION | 지역별 |
| INDUSTRY | 산업별 |
| CURRENCY | 통화별 |

---

## 6. 약어 목록

| 약어 | 전체 표현 | 설명 |
|-----|----------|------|
| TWR | Time-Weighted Return | 시간가중수익률 |
| IRR | Internal Rate of Return | 내부수익률 |
| MDD | Maximum Drawdown | 최대낙폭 |
| PnL | Profit and Loss | 손익 |
| NAV | Net Asset Value | 순자산가치 |
| SSOT | Single Source of Truth | 유일한 진실의 원천 |
| VO | Value Object | 값 객체 |
| DTO | Data Transfer Object | 데이터 전달 객체 |
| DSL | Domain-Specific Language | 도메인 특화 언어 |
| JPA | Java Persistence API | ORM 표준 |
| ECB | European Central Bank | 유럽중앙은행 (환율 소스) |

---

## 7. 통화 코드 (주요)

| 코드 | 통화 | 표준 |
|-----|------|-----|
| USD | 미국 달러 | ISO 4217 |
| KRW | 한국 원 | ISO 4217 |
| EUR | 유로 | ISO 4217 |
| JPY | 일본 엔 | ISO 4217 |
| GBP | 영국 파운드 | ISO 4217 |
| BTC | 비트코인 | 크립토 |
| ETH | 이더리움 | 크립토 |

---

## 8. 관련 문서

- [PRD](./prd.md) - 제품 요구사항
- [요구사항](./requirements.md) - 기능/비기능 요건
- [데이터 설계](./data-design.md) - ERD, 테이블 정의
- [서비스 명세](./service-spec.md) - REST API 명세
- [기획서](./planning.md) - 아키텍처, 기술 스택

---

## 변경 이력

| 버전 | 날짜 | 작성자 | 변경 내용 |
|------|------|--------|----------|
| 1.0.0 | 2026-02-11 | tsq-planner | 초기 작성 — 금융 도메인 용어, 엔티티, 열거형 정의 |
