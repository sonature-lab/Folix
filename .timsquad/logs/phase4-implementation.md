# Phase 4 작업 로그 - 2026-02-11

---

## 21:00 | Phase 4: 외부 API 연동 + Redis 캐싱

### What (작업 내용)
- **Application 레이어**: MarketDataUseCase/Service, ExchangeRateProvider, MarketDataProvider 포트, 예외 클래스 추가
- **Infrastructure 레이어**: Yahoo Finance/CoinGecko/ECB 클라이언트, Redis 캐시 데코레이터, RetryableRestClient, ExternalApiProperties
- **API 레이어**: MarketDataController, MarketDataDto, ServiceConfig/GlobalExceptionHandler 수정, application.yml 프로퍼티 추가
- **테스트**: MarketDataServiceTest, CachedMarketDataProviderTest, CachedExchangeRateProviderTest, EcbXmlParserTest, MarketDataControllerTest

### Why (작업 이유)
Phase 4 요구사항(FR-EXT-001~004) 충족:
- Yahoo Finance 주식 시세 연동
- CoinGecko 크립토 시세 연동
- ECB 환율 데이터 연동
- Redis 캐싱으로 외부 API 호출 최소화 및 응답 성능 개선

### Reference (근거/참조)
| 유형 | 링크 | 설명 |
|-----|------|-----|
| SSOT | [requirements.md#1.9](../ssot/requirements.md#19-외부-연동-phase-4) | FR-EXT-001~004 |
| SSOT | [service-spec.md#8](../ssot/service-spec.md#8-market-data) | Market Data API 명세 |
| Plan | .claude/plans/lovely-snuggling-dahl.md | Phase 4 구현 계획 |

### 변경 파일
```
folix-application/src/main/kotlin/com/folix/application/exception/ApplicationException.kt (수정)
folix-application/src/main/kotlin/com/folix/application/port/out/MarketDataProvider.kt (신규)
folix-application/src/main/kotlin/com/folix/application/port/out/ExchangeRateProvider.kt (신규)
folix-application/src/main/kotlin/com/folix/application/port/in/MarketDataUseCase.kt (신규)
folix-application/src/main/kotlin/com/folix/application/service/MarketDataService.kt (신규)

folix-infrastructure/src/main/kotlin/com/folix/infrastructure/config/RedisConfig.kt (신규)
folix-infrastructure/src/main/kotlin/com/folix/infrastructure/config/ExternalApiConfig.kt (신규)
folix-infrastructure/src/main/kotlin/com/folix/infrastructure/external/common/ExternalApiProperties.kt (신규)
folix-infrastructure/src/main/kotlin/com/folix/infrastructure/external/common/RetryableRestClient.kt (신규)
folix-infrastructure/src/main/kotlin/com/folix/infrastructure/external/common/RestClientConfig.kt (신규)
folix-infrastructure/src/main/kotlin/com/folix/infrastructure/external/yahoo/YahooFinanceClient.kt (신규)
folix-infrastructure/src/main/kotlin/com/folix/infrastructure/external/yahoo/YahooChartResponse.kt (신규)
folix-infrastructure/src/main/kotlin/com/folix/infrastructure/external/coingecko/CoinGeckoClient.kt (신규)
folix-infrastructure/src/main/kotlin/com/folix/infrastructure/external/coingecko/CoinGeckoMarketChartResponse.kt (신규)
folix-infrastructure/src/main/kotlin/com/folix/infrastructure/external/ecb/EcbExchangeRateClient.kt (신규)
folix-infrastructure/src/main/kotlin/com/folix/infrastructure/external/ecb/EcbXmlParser.kt (신규)
folix-infrastructure/src/main/kotlin/com/folix/infrastructure/cache/CachedMarketDataProvider.kt (신규)
folix-infrastructure/src/main/kotlin/com/folix/infrastructure/cache/CachedExchangeRateProvider.kt (신규)
folix-infrastructure/build.gradle.kts (수정)

folix-api/src/main/kotlin/com/folix/api/dto/MarketDataDto.kt (신규)
folix-api/src/main/kotlin/com/folix/api/controller/MarketDataController.kt (신규)
folix-api/src/main/kotlin/com/folix/api/config/ServiceConfig.kt (수정)
folix-api/src/main/kotlin/com/folix/api/common/GlobalExceptionHandler.kt (수정)
folix-api/src/main/resources/application.yml (수정)

folix-application/src/test/kotlin/com/folix/application/service/MarketDataServiceTest.kt (신규)
folix-infrastructure/src/test/kotlin/com/folix/infrastructure/cache/CachedMarketDataProviderTest.kt (신규)
folix-infrastructure/src/test/kotlin/com/folix/infrastructure/cache/CachedExchangeRateProviderTest.kt (신규)
folix-infrastructure/src/test/kotlin/com/folix/infrastructure/external/ecb/EcbXmlParserTest.kt (신규)
folix-api/src/test/kotlin/com/folix/api/controller/MarketDataControllerTest.kt (신규)
```

### 결과
- ✅ 성공
- 테스트: 전체 통과
  - 도메인: 18 tests
  - 애플리케이션: 14 tests
  - 인프라: 4 tests
  - API: 21 tests
  - **합계: 57 tests 통과**
- 빌드: BUILD SUCCESSFUL

### 후속 작업
- [ ] Phase 5: Documentation + Deployment
  - README.md 업데이트
  - ADR 작성
  - Prometheus 연동
  - Docker Compose 설정
  - OCI 배포 준비

---

## 주요 구현 특징

### 1. 외부 API 클라이언트
- **Yahoo Finance**: 주식 시세 조회 (Chart API v8)
- **CoinGecko**: 크립토 시세 조회 (Simple Price API)
- **ECB**: 환율 조회 (Daily XML Feed)

### 2. 재시도 로직
- `RetryableRestClient`: 외부 API 호출 실패 시 자동 재시도 (최대 3회)
- 지수 백오프 (exponential backoff) 적용

### 3. Redis 캐싱
- 시세 데이터: 5분 TTL
- 환율 데이터: 1시간 TTL
- Cache-Aside 패턴 적용

### 4. 에러 처리
- 외부 API 실패 시 ApplicationException 발생
- 캐시 실패 시 원본 Provider로 fallback
- 명확한 에러 메시지 및 로깅

---
