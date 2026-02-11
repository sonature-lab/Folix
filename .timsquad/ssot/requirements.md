# Folix 요건 정의서

**Version**: 1.0
**Created**: 2026-02-11
**PRD Reference**: [prd.md](./prd.md)

---

## 1. 기능 요건 (Functional Requirements)

### 1.1 Money 패키지 (Phase 1)

| ID | 요건 | 우선순위 | 상태 |
|----|-----|:--------:|:----:|
| FR-MONEY-001 | Currency: companion object로 USD, KRW, BTC 등 사전 정의 + of(code) 팩토리 | P0 | ✅ |
| FR-MONEY-002 | Money: BigDecimal + Currency, 연산자 오버로딩 (plus, minus, times) | P0 | ✅ |
| FR-MONEY-003 | Kotlin DSL: `val price = 10_000.USD`, `val btc = 0.5.BTC` | P0 | ✅ |
| FR-MONEY-004 | Wallet: 다통화 보유, totalIn()으로 기준통화 변환 | P0 | ✅ |
| FR-MONEY-005 | ExchangeRate: 환율 변환, 역변환 | P0 | ✅ |
| FR-MONEY-006 | 통화 불일치 연산 시 require()로 즉시 실패 | P0 | ✅ |
| FR-MONEY-007 | 나눗셈 시 반드시 scale + RoundingMode 지정 | P0 | ✅ |

### 1.2 Asset 관리 (Phase 1)

| ID | 요건 | 우선순위 | 상태 |
|----|-----|:--------:|:----:|
| FR-ASSET-001 | 자산 등록 (주식, 크립토, 채권, 현금, 부동산 등) | P0 | ✅ |
| FR-ASSET-002 | 자산 유형별 분류 (AssetType, AssetClass) | P0 | ✅ |
| FR-ASSET-003 | 자산 목록 조회 및 필터링 | P0 | ✅ |

### 1.3 Account 관리 (Phase 1)

| ID | 요건 | 우선순위 | 상태 |
|----|-----|:--------:|:----:|
| FR-ACCT-001 | 계좌 등록 (증권, 은행, 거래소 등) | P0 | ✅ |
| FR-ACCT-002 | 계좌별 자산/거래 분리 관리 | P0 | ✅ |

### 1.4 Transaction 관리 (Phase 1)

| ID | 요건 | 우선순위 | 상태 |
|----|-----|:--------:|:----:|
| FR-TXN-001 | 거래 기록 (매수/매도/배당/이자/입금/출금) | P0 | ✅ |
| FR-TXN-002 | CSV/JSON 일괄 임포트 | P0 | 🔲 |
| FR-TXN-003 | 거래 내역 조회 (기간별, 계좌별, 자산별) | P0 | ✅ |

### 1.5 Portfolio (Phase 1)

| ID | 요건 | 우선순위 | 상태 |
|----|-----|:--------:|:----:|
| FR-PORT-001 | 포트폴리오 요약 (총 자산가치, 수익률) | P0 | ✅ |
| FR-PORT-002 | 보유 포지션 목록 (수량, 현재가, 평가금액) | P0 | ✅ |
| FR-PORT-003 | 자산 배분 현황 (유형별, 통화별 비중) | P0 | ✅ |

### 1.6 Performance 계산 (Phase 2)

| ID | 요건 | 우선순위 | 상태 |
|----|-----|:--------:|:----:|
| FR-PERF-001 | TWR (Time-Weighted Return) 계산 | P0 | ✅ |
| FR-PERF-002 | IRR (Internal Rate of Return) — Newton-Raphson 방법 | P0 | ✅ |
| FR-PERF-003 | 연환산 변동성 (Annualized Volatility) | P1 | ✅ |
| FR-PERF-004 | 샤프비율 (Sharpe Ratio) | P1 | ✅ |
| FR-PERF-005 | 최대낙폭 MDD (Maximum Drawdown) | P1 | ✅ |

### 1.7 Simulation (Phase 2)

| ID | 요건 | 우선순위 | 상태 |
|----|-----|:--------:|:----:|
| FR-SIM-001 | Scenario: "만약 ~했더라면" 시나리오 정의 | P1 | ✅ |
| FR-SIM-002 | MonteCarlo: 10,000회 시뮬레이션 → 퍼센타일 결과 | P1 | ✅ |
| FR-SIM-003 | NetWorthTimeline: 순자산 시계열 추적 | P1 | ✅ |
| FR-SIM-004 | Projection: 미래 자산 예측 | P1 | ✅ |

### 1.8 Taxonomy (Phase 2)

| ID | 요건 | 우선순위 | 상태 |
|----|-----|:--------:|:----:|
| FR-TAX-001 | 트리 구조 분류 체계 (자산유형, 지역, 산업, 통화 축) | P1 | ✅ |
| FR-TAX-002 | 하나의 자산이 여러 분류에 가중치(%)로 매핑 | P1 | ✅ |

### 1.9 외부 연동 (Phase 4)

| ID | 요건 | 우선순위 | 상태 |
|----|-----|:--------:|:----:|
| FR-EXT-001 | Yahoo Finance 주식 시세 조회 | P1 | ✅ |
| FR-EXT-002 | CoinGecko 크립토 시세 조회 | P1 | ✅ |
| FR-EXT-003 | ECB 환율 조회 | P1 | ✅ |
| FR-EXT-004 | Redis 캐싱으로 외부 API 호출 최소화 | P1 | ✅ |

---

## 2. 비기능 요건 (Non-Functional Requirements)

### 2.1 성능

| ID | 요건 | 목표치 | 측정 방법 |
|----|-----|-------|----------|
| NFR-PERF-001 | API 응답 시간 | < 200ms (p95) | Spring Actuator |
| NFR-PERF-002 | 몬테카를로 시뮬레이션 (10K회) | < 5s | 벤치마크 테스트 |
| NFR-PERF-003 | 포트폴리오 성과 계산 | < 1s (1년 일별 데이터) | 벤치마크 테스트 |

### 2.2 정확성

| ID | 요건 | 목표치 |
|----|-----|-------|
| NFR-ACC-001 | TWR/IRR 계산 정확도 | Portfolio Performance 결과 대비 오차 0.01% 이내 |
| NFR-ACC-002 | BigDecimal 스케일 | 금액 8자리, 환율 10자리 |

### 2.3 코드 품질

| ID | 요건 | 목표치 |
|----|-----|-------|
| NFR-QUAL-001 | 도메인 모듈 테스트 커버리지 | 90% 이상 |
| NFR-QUAL-002 | Detekt 정적 분석 | 경고 0개 |
| NFR-QUAL-003 | 아키텍처 위반 | ArchUnit 검증 통과 |

### 2.4 확장성

| ID | 요건 | 상세 |
|----|-----|-----|
| NFR-SCALE-001 | 컨테이너 기반 배포 | Docker Compose |
| NFR-SCALE-002 | 모듈 독립성 | 각 모듈 독립 빌드 가능 |

---

## 3. 제약 조건

### 3.1 기술적 제약
- 기술 스택 절대 변경 금지 (섹션 3 참조)
- domain 모듈에 Spring, JPA 등 외부 프레임워크 의존성 금지
- 모든 금액 계산은 BigDecimal (Double/Float 절대 금지)
- JPA 엔티티와 도메인 엔티티 반드시 분리
- Controller에서 비즈니스 로직 금지
- DTO는 도메인 객체 직접 노출 금지

### 3.2 환경 제약
- 1인 개발 프로젝트
- OCI Free Tier 배포 대상

### 3.3 라이선스
- Ghostfolio (AGPL v3) 코드 복사 금지 — 설계 참고만
- Roboquant (Apache 2.0) — 설계 참고만, 포크 아님

---

## 4. 인터페이스 요건

### 4.1 사용자 인터페이스
- REST API만 제공 (프론트엔드 없음)
- Swagger UI로 API 문서 자동 생성

### 4.2 외부 시스템 인터페이스
| 시스템 | 연동 방식 | 목적 |
|-------|---------|-----|
| Yahoo Finance | REST API | 주식 시세 |
| CoinGecko | REST API | 크립토 시세 |
| ECB | REST API / XML | 환율 |
| PostgreSQL | JDBC (JPA) | 데이터 영속화 |
| Redis | Lettuce | 시세 캐싱 |

---

## 5. 코딩 표준

### 5.1 Kotlin 스타일
- data class 적극 활용 (VO, DTO)
- sealed class/interface 활용 (타입 안전성)
- 확장함수로 DSL 스타일 API
- null 안전성 — lateinit/!! 사용 최소화
- 불변 우선 (val > var, List > MutableList)

### 5.2 명명 규칙
- 패키지: 소문자 (`com.folix.domain.money`)
- 클래스: PascalCase (`TimeWeightedReturn`)
- 함수: camelCase (`calculatePerformance`)
- 상수: UPPER_SNAKE_CASE (`DEFAULT_SCALE = 8`)
- 테스트: `should_동작_when_조건` 또는 Kotest `describe/it`

### 5.3 필수 사항
- BigDecimal + RoundingMode로 모든 금융 계산
- require() / check()로 선행 조건 검증
- 모든 public 함수에 KDoc 주석
- 에러 처리 시 의미있는 예외 메시지
- 테스트에서 경계값/에지케이스 포함
- 커밋 메시지: Conventional Commits (feat:, fix:, refactor:, test:, docs:)

---

## 6. 상태 범례

| 상태 | 의미 |
|-----|------|
| 🔲 | 미착수 |
| 🔄 | 진행중 |
| ✅ | 완료 |
| ❌ | 취소/제외 |

---

## 7. 변경 이력

| 버전 | 날짜 | 변경 내용 | 작성자 |
|-----|-----|----------|-------|
| 1.0 | 2026-02-11 | 최초 작성 | ericson |
