# Folix 기획서

**Version**: 1.0
**Created**: 2026-02-11
**PRD Reference**: [prd.md](./prd.md)

---

## 1. 프로젝트 개요

### 1.1 요약
Kotlin + Spring Boot 기반 개인 자산 모델링 엔진. Clean Architecture 멀티모듈 구조로
멀티통화 포트폴리오 관리, 성과 계산, 리스크 분석, 몬테카를로 시뮬레이션을 제공하는 REST API 백엔드.

### 1.2 범위
- **In Scope**: 백엔드 엔진 (도메인 모델, 유즈케이스, REST API, 인프라)
- **Out of Scope**: 프론트엔드 UI, 사용자 인증, 실시간 트레이딩

---

## 2. 마일스톤

| Phase | 목표 | 산출물 | 완료 기준 |
|-------|-----|--------|----------|
| Phase 1 | 코어 도메인 | money/, asset/, account/, transaction/, portfolio/ | 순수 Kotlin, 의존성 없음, 단위 테스트 통과 |
| Phase 2 | 성과 계산 엔진 | performance/, simulation/ | TWR/IRR, RiskMetrics, MonteCarlo 구현 + 테스트 |
| Phase 3 | API + 인프라 | Spring Boot, JPA, Controller, DTO | Docker Compose 실행, Swagger UI 동작 |
| Phase 4 | 외부 연동 | Yahoo Finance, CoinGecko, ECB | Redis 캐싱, 외부 API 에러 핸들링 |
| Phase 5 | 문서 + 배포 | README.md/ko.md, ADR, Docker | Prometheus + Grafana, OCI 배포 |

### Phase 완료 조건 (모든 Phase 공통)
- [ ] 모든 클래스에 단위 테스트 존재
- [ ] 컴파일 에러 없음
- [ ] 아키텍처 위반 없음 (의존성 방향 검증)
- [ ] Phase 완료 보고 후 사용자 승인

---

## 3. 기술 스택

### 3.1 Backend
| 기술 | 용도 | 선택 이유 |
|-----|------|----------|
| Kotlin 2.x | 언어 | Java 호환, 확장함수/DSL 활용, null 안전성 |
| Spring Boot 3.x | 프레임워크 | Clean Architecture 적합, 생태계 |
| Gradle Kotlin DSL | 빌드 | 멀티모듈 관리 |
| Spring Data JPA | ORM | ACID 보장 |
| PostgreSQL | DB | ACID, JSON 지원 |

### 3.2 Infrastructure
| 기술 | 용도 | 선택 이유 |
|-----|------|----------|
| Redis | 캐시 | 시세 데이터 캐싱 |
| Docker + Docker Compose | 컨테이너 | PostgreSQL + Redis + App |
| Prometheus + Grafana | 모니터링 | OCI Free Tier 배포 |

### 3.3 Testing
| 기술 | 용도 | 선택 이유 |
|-----|------|----------|
| JUnit 5 | 테스트 프레임워크 | 표준 |
| Kotest | Kotlin 테스트 DSL | describe/it 스타일 |
| Testcontainers | 통합 테스트 | 실제 DB/Redis 환경 |

### 3.4 API
| 기술 | 용도 | 선택 이유 |
|-----|------|----------|
| REST | API 스타일 | 표준, Swagger 자동 생성 |
| OpenAPI 3.0 | API 문서 | Swagger UI 자동 생성 |

---

## 4. 아키텍처 개요

```
Clean Architecture — 4-Layer Multi-Module

┌──────────────────────────────────────────────┐
│                folix-api                │
│         (Controllers, DTOs, Config)          │
├──────────────────────────────────────────────┤
│            folix-application            │
│     (UseCases, Inbound/Outbound Ports)       │
├──────────────────────────────────────────────┤
│              folix-domain               │
│   (Entities, Value Objects, Domain Logic)    │
│        ⚠ 외부 의존성 ZERO                    │
├──────────────────────────────────────────────┤
│          folix-infrastructure           │
│    (JPA, Redis, External APIs, Parsers)      │
└──────────────────────────────────────────────┘

의존성 방향 (절대 역방향 금지):
api → application → domain
                ↑
          infrastructure
```

### 4.1 주요 컴포넌트 (모듈별)

| 모듈 | 역할 | 기술 |
|------|-----|-----|
| folix-domain | 순수 도메인 모델, 금융 계산 | 순수 Kotlin (Spring/JPA 금지) |
| folix-application | 유즈케이스, 포트 정의 | domain만 의존 |
| folix-infrastructure | Outbound Port 구현 | JPA, Redis, 외부 API |
| folix-api | REST 진입점 | Spring Boot, Controller, DTO |

---

## 5. 도메인 패키지 구조

```
folix-domain/src/main/kotlin/com/folix/domain/
├── money/          # Currency, Money, Wallet, ExchangeRate, DSL
├── asset/          # Asset, AssetType, AssetClass
├── account/        # Account, AccountType
├── transaction/    # Transaction, TransactionType
├── portfolio/      # Portfolio, Position, Allocation
├── taxonomy/       # Classification, TaxonomyNode
├── performance/    # TWR, IRR, RiskMetrics (Phase 2)
└── simulation/     # Scenario, MonteCarlo, Projection (Phase 2)
```

---

## 6. 팀 구성

| 역할 | 담당 | 책임 |
|-----|-----|-----|
| PM + Architect + Developer | Claude Code | 설계 제안 + 코드 구현 |
| Product Owner | ericson | 요구사항 정의, Phase 승인 |

---

## 7. 의존성

### 7.1 외부 의존성
| 서비스/API | 용도 | 대안 |
|-----------|-----|-----|
| Yahoo Finance API | 주식 시세 | Alpha Vantage |
| CoinGecko API | 크립토 시세 | CoinMarketCap |
| ECB Exchange Rate | 환율 | Open Exchange Rates |

### 7.2 내부 의존성
- Phase 2는 Phase 1 도메인 모델에 의존
- Phase 3은 Phase 1 + 2 완료 후 진행
- Phase 4는 Phase 3 인프라 레이어 필요

---

## 8. 리스크 관리

| 리스크 | 확률 | 영향 | 대응 계획 |
|-------|-----|-----|----------|
| BigDecimal 정밀도 이슈 | 중 | 높 | 나눗셈 시 scale + RoundingMode 필수, 테스트 대조 |
| Clean Architecture 위반 | 중 | 높 | ArchUnit 자동 검증, 코드 리뷰 |
| 외부 API rate limit | 높 | 중 | Redis 캐싱, exponential backoff |
| 몬테카를로 성능 | 중 | 중 | 코루틴 병렬 처리, 결과 캐싱 |

---

## 9. 변경 이력

| 버전 | 날짜 | 변경 내용 | 작성자 |
|-----|-----|----------|-------|
| 1.0 | 2026-02-11 | 최초 작성 | ericson |
