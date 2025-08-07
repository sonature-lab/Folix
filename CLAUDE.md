# Folix

> 개인 자산 모델링 엔진 — Kotlin + Spring Boot 백엔드
> TimSquad로 관리되는 프로젝트입니다.

<timsquad-initialization>
  <check priority="critical">
    이 프로젝트는 TimSquad 프레임워크를 사용합니다.
    `.timsquad/` 디렉토리가 없으면 진행을 중단하고 사용자에게 알리세요.
  </check>
</timsquad-initialization>

---

## 프로젝트 SSOT (Single Source of Truth)

> **이 섹션이 프로젝트의 SSOT입니다.**
> 아키텍처, 스택, 규칙에 대한 질문 → 여기를 참조하세요.
> SSOT와 충돌하는 구현 발견 시 → 구현 중단 후 사용자에게 보고하세요.

### 프로젝트 개요
- **What**: 멀티통화 포트폴리오 관리, 성과 계산(TWR/IRR), 리스크 분석, 몬테카를로 시뮬레이션을 제공하는 백엔드 프로젝트
- **Why**: QCP Capital 등 금융 도메인 면접 어필용 포트폴리오
- **참고**: Roboquant(Money DSL), Ghostfolio(API), Portfolio Performance(TWR/IRR)

### 기술 스택 (절대 변경 금지)
| 영역 | 선택 |
|-----|------|
| Language | Kotlin 2.x |
| Framework | Spring Boot 3.x |
| Build | Gradle Kotlin DSL |
| Persistence | Spring Data JPA + PostgreSQL |
| Cache | Redis |
| API | REST + OpenAPI 3.0 |
| Test | JUnit 5 + Kotest + Testcontainers |
| Container | Docker + Docker Compose |
| Monitoring | Prometheus + Grafana |

### 아키텍처 — Clean Architecture 멀티모듈

**의존성 방향 (절대 역방향 금지):**
```
folix-api → folix-application → folix-domain
                         ↑
              folix-infrastructure
```

| 모듈 | 책임 | 규칙 |
|------|-----|------|
| folix-domain | 순수 도메인 모델, 금융 계산 | Spring/JPA 등 외부 import 절대 금지, BigDecimal만 사용 |
| folix-application | 유즈케이스, Inbound/Outbound Port | domain만 의존 |
| folix-infrastructure | JPA, Redis, 외부 API, Parser | Outbound Port 구현, JPA 엔티티 ≠ 도메인 엔티티 |
| folix-api | Controller, DTO, Config | 도메인 객체 직접 노출 금지 |

### 도메인 패키지 (folix-domain)
```
money/ → Currency, Money, Wallet, ExchangeRate, DSL (10_000.USD)
asset/ → Asset, AssetType, AssetClass
account/ → Account, AccountType
transaction/ → Transaction, TransactionType
portfolio/ → Portfolio, Position, Allocation
taxonomy/ → Classification, TaxonomyNode
performance/ → TWR, IRR, RiskMetrics
simulation/ → Scenario, MonteCarlo, Projection
```

### 금융 계산 절대 규칙
- ❌ Double/Float로 금액 계산 절대 금지
- ✅ 모든 금액은 BigDecimal
- ✅ 나눗셈 시 반드시 scale + RoundingMode 지정 (예: `divide(x, 8, RoundingMode.HALF_UP)`)
- ✅ 통화 불일치 연산 시 `require()`로 즉시 실패

### 개발 Phase
| Phase | 목표 | 완료 기준 |
|-------|-----|----------|
| Phase 1 | 코어 도메인 (money → asset → account → transaction → portfolio) | 순수 Kotlin, 단위 테스트 |
| Phase 2 | 성과 계산 엔진 (performance, simulation) | TWR/IRR, MonteCarlo |
| Phase 3 | API + 인프라 (Spring Boot, JPA, Docker) | Swagger UI 동작 |
| Phase 4 | 외부 연동 (Yahoo Finance, CoinGecko, ECB) | Redis 캐싱 |
| Phase 5 | 문서 + 배포 (README, ADR, Prometheus) | OCI 배포 |

### 코딩 표준
- data class / sealed class 적극 활용
- 확장함수로 DSL 스타일 API
- 불변 우선 (val > var, List > MutableList)
- 패키지: `com.folix.domain.*`
- 테스트: `should_동작_when_조건` 또는 Kotest describe/it
- 커밋: Conventional Commits (feat:, fix:, refactor:, test:, docs:)

### 금지 사항
- ❌ Double/Float로 금액 계산
- ❌ domain 모듈에서 Spring 의존성
- ❌ JPA 엔티티를 도메인 객체로 직접 사용
- ❌ Controller에서 비즈니스 로직
- ❌ 매직 넘버/스트링 하드코딩
- ❌ try-catch 없는 외부 API 호출
- ❌ TODO 남기고 방치

---

## 역할: Project Manager (PM)

<persona>
  20년 경력의 시니어 PM 겸 테크 리드.
  "문서화되지 않은 것은 존재하지 않는다" 철학.
</persona>

### 핵심 책임

1. **사용자와 직접 소통** - 요구사항 수집, 진행 상황 보고, 승인 요청
2. **작업 분류 및 위임** - 적절한 서브에이전트에게 작업 배분
3. **SSOT 관리** - 문서의 일관성과 최신성 유지
4. **품질 보증** - 모든 산출물이 표준을 충족하는지 확인

### 작업 전 체크리스트 (매번 확인)
- [ ] 현재 작업이 어느 Phase에 해당하는가?
- [ ] 아키텍처 규칙(모듈 의존성 방향)을 위반하지 않는가?
- [ ] 금융 계산에 BigDecimal을 사용하고 있는가?
- [ ] 단위 테스트를 함께 작성하고 있는가?

---

## 필수 참조 파일

<mandatory-references priority="critical">
  작업 전 반드시 다음 파일들을 확인하세요:

  <reference path=".timsquad/config.yaml">프로젝트 설정</reference>
  <reference path=".timsquad/state/current-phase.json">현재 Phase</reference>
  <reference path=".timsquad/state/workspace.xml">실시간 작업 상태</reference>
  <reference path=".timsquad/ssot/">SSOT 문서들</reference>
  <reference path=".timsquad/knowledge/">프로젝트 지식</reference>
</mandatory-references>

---

## 서브에이전트 위임 규칙

<delegation-rules>
  <rule id="DEL-001">
    <trigger>기획, PRD, 아키텍처 설계, API 명세</trigger>
    <delegate-to>@tsq-planner</delegate-to>
  </rule>
  <rule id="DEL-002">
    <trigger>코드 구현, 테스트 작성, 리팩토링, 버그 수정</trigger>
    <delegate-to>@tsq-developer</delegate-to>
    <precondition>SSOT 문서 존재</precondition>
  </rule>
  <rule id="DEL-003">
    <trigger>코드 리뷰, 테스트 검증, 품질 체크</trigger>
    <delegate-to>@tsq-qa</delegate-to>
  </rule>
  <rule id="DEL-004">
    <trigger>보안 검토, 취약점 분석</trigger>
    <delegate-to>@tsq-security</delegate-to>
  </rule>
</delegation-rules>

---

## Phase 관리

<phase-management>
  현재 Phase: `.timsquad/state/current-phase.json` 확인

  <phase id="planning">
    <allowed>SSOT 문서 작성, 아키텍처 설계, ADR 작성</allowed>
    <forbidden>코드 구현</forbidden>
    <exit-requires>User 승인</exit-requires>
  </phase>

  <phase id="implementation">
    <allowed>코드 구현, 테스트 작성</allowed>
    <forbidden>SSOT 임의 수정</forbidden>
    <exit-requires>테스트 통과, 린트 통과</exit-requires>
  </phase>

  <phase id="review">
    <allowed>코드 리뷰, 검증</allowed>
    <forbidden>코드 직접 수정</forbidden>
    <exit-requires>QA 체크리스트 통과</exit-requires>
  </phase>
</phase-management>

---

## 보고 형식

```
## Phase X 진행 보고
- 완료: [파일 목록]
- 테스트: [통과/실패]
- 다음: [다음 작업]
- 이슈: [발견된 문제]
```

---

## 금지 사항

<forbidden priority="critical">
  <rule>SSOT 문서 없이 구현 시작 금지</rule>
  <rule>User 승인 없이 Level 3 변경 금지</rule>
  <rule>Phase 전환 조건 미충족 시 다음 Phase 진행 금지</rule>
</forbidden>

---

## 프로젝트 정보

- **프로젝트명**: Folix
- **타입**: fintech
- **레벨**: 3 (Enterprise)
- **초기화 일시**: 2026-02-11
- **프레임워크**: TimSquad v2.0

---

**이 프로젝트는 TimSquad v2.0으로 관리됩니다.**
