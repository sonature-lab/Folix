# Folix

**[English](README.md)**

> 멀티통화 포트폴리오 모델링 엔진 — Kotlin + Spring Boot

Folix는 개인 포트폴리오 관리를 위한 백엔드 엔진입니다. 멀티통화 지원, 성과 계산(TWR/IRR), 리스크 분석, 몬테카를로 시뮬레이션을 제공합니다. 순수 Kotlin으로 **Clean Architecture**와 **Domain-Driven Design** 원칙에 따라 설계되었습니다.

---

## 아키텍처

의존성 방향이 엄격하게 제어되는 Clean Architecture 멀티모듈 구조:

```
folix-api  →  folix-application  →  folix-domain
                     ↑
            folix-infrastructure
```

| 모듈 | 책임 |
|------|------|
| **folix-domain** | 순수 도메인 모델, 금융 계산 (프레임워크 의존성 없음) |
| **folix-application** | 유즈케이스, Inbound/Outbound 포트 |
| **folix-infrastructure** | JPA 엔티티, 레포지토리 어댑터, 외부 API 클라이언트 |
| **folix-api** | REST 컨트롤러, DTO, Spring Boot 설정 |

---

## 도메인 패키지

```
com.folix.domain
├── money/          Currency, Money, Wallet, ExchangeRate, DSL
├── asset/          Asset, AssetType, AssetClass
├── account/        Account, AccountType
├── transaction/    Transaction, TransactionType
├── portfolio/      Portfolio, Position, Allocation
├── taxonomy/       TaxonomyNode, Classification
├── performance/    TWR, IRR, RiskMetrics (Sharpe, Sortino, MaxDrawdown)
└── simulation/     MonteCarloSimulation, Scenario, Projection
```

---

## 주요 기능

- **Money DSL** — `BigDecimal` 정밀도의 타입 안전한 화폐 연산
- **멀티통화** — 환율 변환을 지원하는 Wallet
- **포트폴리오 분석** — 포지션 추적, 유형/지역/통화별 자산 배분
- **성과 엔진** — 시간가중수익률(TWR), 내부수익률(IRR)
- **리스크 지표** — 샤프 비율, 소르티노 비율, 최대 낙폭, 변동성
- **몬테카를로 시뮬레이션** — 신뢰구간 포함 시나리오 분석
- **택소노미** — 계층적 자산 분류 체계

---

## 기술 스택

| 영역 | 선택 |
|------|------|
| 언어 | Kotlin 2.1 |
| 프레임워크 | Spring Boot 3.4 |
| 빌드 | Gradle Kotlin DSL |
| 영속성 | Spring Data JPA + PostgreSQL 16 |
| 캐시 | Redis 7 |
| API | REST + OpenAPI 3.0 (Swagger UI) |
| 테스트 | Kotest + MockK + JUnit 5 |
| 컨테이너 | Docker + Docker Compose |
| JDK | 21 |

---

## 시작하기

### 사전 요구사항

- JDK 21+
- Docker & Docker Compose

### 1. 클론 및 인프라 실행

```bash
git clone https://github.com/sonature-lab/Folix.git
cd Folix
docker compose up -d
```

### 2. 애플리케이션 실행

```bash
./gradlew :folix-api:bootRun --args='--spring.profiles.active=local'
```

### 3. Swagger UI 접속

```
http://localhost:8080/swagger-ui.html
```

### 4. 테스트 실행

```bash
./gradlew test
```

---

## API 엔드포인트

| 메서드 | 엔드포인트 | 설명 |
|--------|----------|------|
| `POST` | `/api/v1/assets` | 자산 등록 |
| `GET` | `/api/v1/assets` | 자산 목록 조회 (`?type=`으로 필터) |
| `GET` | `/api/v1/assets/{id}` | 자산 단건 조회 |
| `POST` | `/api/v1/accounts` | 계좌 등록 |
| `GET` | `/api/v1/accounts` | 계좌 목록 조회 |
| `GET` | `/api/v1/accounts/{id}` | 계좌 단건 조회 |
| `POST` | `/api/v1/transactions` | 거래 기록 |
| `GET` | `/api/v1/transactions` | 거래 목록 조회 (계좌/자산/기간 필터) |
| `GET` | `/api/v1/transactions/{id}` | 거래 단건 조회 |
| `GET` | `/api/v1/portfolio` | 포트폴리오 요약 |
| `GET` | `/api/v1/portfolio/positions` | 보유 포지션 |
| `GET` | `/api/v1/portfolio/allocation` | 자산 배분 |
| `GET` | `/api/v1/portfolio/performance` | 성과 (TWR/IRR) |
| `GET` | `/api/v1/health` | 헬스체크 |

---

## 프로젝트 구조

```
Folix/
├── folix-domain/              # 순수 도메인 모델 & 금융 로직
├── folix-application/         # 유즈케이스 & 포트 인터페이스
├── folix-infrastructure/      # JPA 어댑터 & 외부 연동
├── folix-api/                 # REST API & Spring Boot 앱
├── docker-compose.yml         # PostgreSQL + Redis
├── Dockerfile                 # 프로덕션 컨테이너 이미지
├── build.gradle.kts           # 루트 빌드 설정
└── settings.gradle.kts        # 멀티모듈 설정
```

---

## 개발 로드맵

| Phase | 목표 | 상태 |
|-------|------|------|
| Phase 1 | 코어 도메인 (money, asset, account, transaction, portfolio, taxonomy) | 완료 |
| Phase 2 | 성과 엔진 (TWR/IRR, 몬테카를로, 리스크 지표) | 완료 |
| Phase 3 | API + 인프라 (Spring Boot, JPA, Docker) | 완료 |
| Phase 4 | 외부 연동 (Yahoo Finance, CoinGecko, ECB 환율) | 예정 |
| Phase 5 | 배포 + 모니터링 (Prometheus, Grafana, OCI) | 예정 |

---

## 라이선스

이 프로젝트는 개인 포트폴리오 및 교육 목적으로 제작되었습니다.
