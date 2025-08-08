# Folix PRD

**Version**: 1.0
**Created**: 2026-02-11
**Author**: ericson

---

## 1. 개요

### 1.1 한 줄 정의
> 개인 자산 모델링 엔진 — 멀티통화 포트폴리오 관리, 성과 계산(TWR/IRR), 리스크 분석, 몬테카를로 시뮬레이션을 제공하는 Kotlin + Spring Boot 백엔드 프로젝트.

### 1.2 배경
- QCP Capital(싱가포르 크립토 트레이딩) 등 금융 도메인 면접 어필용 포트폴리오 프로젝트
- 트레이딩 시스템의 포지션 관리, PnL 계산과 동일한 도메인 문제를 다룸
- 오픈소스 분석(Roboquant, Ghostfolio, Portfolio Performance)을 기반으로 직접 설계

### 1.3 Before → After
- **Before**: Next.js + FSD 프론트엔드 가계부 MVP
- **After**: Kotlin + Spring Boot 백엔드 자산 모델링 엔진 → REST API → 프론트는 나중에

### 1.4 목표
- **Primary**: 금융 도메인 역량을 증명하는 백엔드 포트폴리오 프로젝트 완성
- **Secondary**: Clean Architecture 기반 확장 가능한 자산 관리 엔진 구축

### 1.5 비목표 (Non-Goals)
이 프로젝트에서 하지 않을 것:
- 프론트엔드 UI 구현 (REST API만 제공)
- 실시간 트레이딩 기능
- 사용자 인증/권한 관리 (포트폴리오 프로젝트 범위 외)
- 실제 금융 거래 연동

---

## 2. 타겟 사용자

### 2.1 Primary User
- **Who**: 면접관 / 기술 리뷰어 (QCP Capital 등 금융 도메인 기업)
- **Pain Point**: 지원자의 금융 도메인 이해도와 코드 품질을 빠르게 평가하기 어려움
- **Need**: 도메인 문제 해결 역량을 보여주는 잘 설계된 코드베이스

### 2.2 Secondary User
- **Who**: 개인 투자자 (추후 프론트엔드 연동 시)
- **Pain Point**: 멀티통화 자산을 통합 관리하고 성과를 분석하기 어려움
- **Need**: 다양한 자산을 한곳에서 관리하고 포트폴리오 성과를 정량적으로 분석

---

## 3. 핵심 기능

### 3.1 Must Have (P0)
| 기능 | 설명 | 성공 기준 |
|-----|------|----------|
| Money 패키지 | BigDecimal 기반 멀티통화 금액 처리, Kotlin DSL | 통화 불일치 연산 시 즉시 실패, 정밀도 보장 |
| Asset 관리 | 자산(주식, 크립토, 채권 등) CRUD | 다양한 자산 유형 지원 |
| Account 관리 | 투자 계좌 관리 | 계좌별 자산/거래 분리 |
| Transaction 기록 | 매수/매도/배당/이자 거래 기록 | CSV/JSON 일괄 임포트 지원 |
| Portfolio 요약 | 보유 포지션, 자산 배분 현황 | 정확한 보유량 및 가치 계산 |
| TWR/IRR 성과 계산 | 시간가중수익률, 내부수익률 | Portfolio Performance 결과와 오차 0.01% 이내 |

### 3.2 Should Have (P1)
| 기능 | 설명 | 성공 기준 |
|-----|------|----------|
| RiskMetrics | 연환산 변동성, 샤프비율, MDD | 금융 공학 표준 공식 준수 |
| Monte Carlo 시뮬레이션 | 10,000회 시뮬레이션 기반 자산 예측 | 5/25/50/75/95 퍼센타일 결과 제공 |
| Taxonomy 분류 | 트리 구조 자산 분류 (유형/지역/산업/통화) | 하나의 자산이 여러 축으로 분류 가능 |
| 시세/환율 조회 | Yahoo Finance, CoinGecko, ECB 연동 | Redis 캐싱으로 API 호출 최소화 |
| 순자산 시계열 | NetWorthTimeline 추적 | 일별 순자산 변화 시각화 데이터 |

### 3.3 Nice to Have (P2)
| 기능 | 설명 |
|-----|------|
| Scenario 분석 | "만약 ~했더라면" 시나리오 시뮬레이션 |
| PDF 파싱 | 증권사 리포트 PDF에서 거래 내역 추출 |
| Prometheus + Grafana | 모니터링 대시보드 |

---

## 4. 성공 지표

### 4.1 정량적 지표
| 지표 | 목표 | 측정 방법 |
|-----|------|----------|
| 테스트 커버리지 | 도메인 모듈 90% 이상 | JaCoCo |
| API 응답 시간 | p95 < 200ms | Spring Actuator |
| TWR/IRR 정확도 | 오차 0.01% 이내 | Portfolio Performance 결과 대조 |
| 코드 품질 | Detekt 경고 0개 | Detekt Kotlin 정적 분석 |

### 4.2 정성적 지표
- Clean Architecture 원칙 준수 (모듈 간 의존성 방향 검증)
- 도메인 모델이 금융 문제를 명확하게 표현
- README가 면접관에게 프로젝트 가치를 효과적으로 전달

---

## 5. 제약사항

### 5.1 기술적 제약
- 기술 스택 절대 변경 금지 (Kotlin, Spring Boot, PostgreSQL, Redis)
- 모든 금액 계산은 BigDecimal (Double/Float 절대 금지)
- domain 모듈에 Spring 의존성 금지

### 5.2 비즈니스 제약
- AGPL 라이선스 코드(Ghostfolio) 복사 금지 — 설계 참고만
- 1인 개발 프로젝트

### 5.3 일정
- **시작**: 2026-02-11
- **목표 완료**: Phase별 점진적 완성

---

## 6. 리스크

| 리스크 | 영향 | 대응 |
|-------|-----|-----|
| 금융 계산 정확도 오류 | 신뢰성 하락 | 경계값 테스트 + 오픈소스 결과 대조 검증 |
| 외부 API(시세) 불안정 | 데이터 갱신 실패 | Redis 캐시 + fallback 전략 |
| Clean Architecture 위반 | 유지보수성 저하 | ArchUnit 의존성 방향 자동 검증 |
| 스코프 확대 | 완료 지연 | Phase별 엄격한 완료 조건 |

---

## 7. 참고

### 7.1 관련 오픈소스
| 프로젝트 | 참고 범위 | 주의 |
|---------|----------|------|
| Roboquant (Kotlin, Apache 2.0) | Money 패키지 설계, Kotlin DSL, 시간 처리 | 포크 아님, 설계 참고만 |
| Ghostfolio (TypeScript, AGPL v3) | API 설계, 멀티계좌 모델, DataSource 추상화 | 코드 복사 금지 (AGPL) |
| Portfolio Performance (Java, EPL 1.0) | TWR/IRR 계산 로직, Taxonomy 분류, PDF 파싱 | 설계 참고, 알고리즘 구현은 독자적 |

### 7.2 용어 정의
| 용어 | 정의 |
|-----|------|
| TWR | Time-Weighted Return — 현금 유출입 영향 제거한 순수 투자 성과 |
| IRR | Internal Rate of Return — Newton-Raphson 방법으로 근사 |
| MDD | Maximum Drawdown — 고점 대비 최대 하락폭 |
| Taxonomy | 트리 구조 자산 분류 체계 |

---

## 8. 면접 스토리라인

> "개인 자산 모델링 프로젝트를 Kotlin/Spring Boot로 설계·구현했습니다.
> 멀티통화 처리, 포트폴리오 성과 계산(TWR/IRR), 리스크 지표(변동성, 샤프비율, MDD)를 구현했고,
> 이는 트레이딩 시스템의 포지션 관리 및 PnL 계산과 동일한 도메인 문제를 다룹니다.
> 차별화 포인트로 몬테카를로 시뮬레이션 기반 자산 예측 엔진을 독자 설계했습니다."
