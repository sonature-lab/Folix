# Folix 데이터 설계서

**Version**: 1.0
**Created**: 2026-02-11
**Database**: PostgreSQL

---

## 1. 설계 원칙

- JPA 엔티티와 도메인 엔티티 **반드시 분리** (infrastructure 레이어에 JPA 엔티티, domain 레이어에 도메인 모델)
- 모든 금액 컬럼은 `NUMERIC(precision, scale)` — Double 사용 금지
- UUID를 Primary Key로 사용
- created_at / updated_at 자동 관리

---

## 2. ERD

```
┌─────────────────┐       ┌─────────────────┐
│    accounts     │       │     assets      │
├─────────────────┤       ├─────────────────┤
│ id (PK, UUID)   │       │ id (PK, UUID)   │
│ name            │       │ symbol          │
│ type            │       │ name            │
│ currency        │       │ type            │
│ institution     │       │ currency        │
│ created_at      │       │ exchange        │
│ updated_at      │       │ created_at      │
└────────┬────────┘       │ updated_at      │
         │                └────────┬────────┘
         │                         │
         │    ┌────────────────────┘
         │    │
         ▼    ▼
┌─────────────────────┐
│    transactions     │
├─────────────────────┤
│ id (PK, UUID)       │
│ account_id (FK)     │
│ asset_id (FK)       │
│ type                │
│ quantity            │  ← NUMERIC(20,8)
│ price               │  ← NUMERIC(20,8)
│ fee                 │  ← NUMERIC(20,8)
│ currency            │
│ transaction_date    │
│ note                │
│ created_at          │
│ updated_at          │
└─────────────────────┘

┌─────────────────────┐
│   daily_prices      │
├─────────────────────┤
│ id (PK, UUID)       │
│ asset_id (FK)       │
│ date                │
│ open                │  ← NUMERIC(20,8)
│ high                │  ← NUMERIC(20,8)
│ low                 │  ← NUMERIC(20,8)
│ close               │  ← NUMERIC(20,8)
│ volume              │
│ source              │
│ created_at          │
└─────────────────────┘

┌─────────────────────┐
│  exchange_rates     │
├─────────────────────┤
│ id (PK, UUID)       │
│ from_currency       │
│ to_currency         │
│ rate                │  ← NUMERIC(20,10)
│ date                │
│ source              │
│ created_at          │
└─────────────────────┘

┌─────────────────────┐       ┌─────────────────────┐
│  taxonomy_nodes     │       │ asset_classifications│
├─────────────────────┤       ├─────────────────────┤
│ id (PK, UUID)       │       │ id (PK, UUID)       │
│ parent_id (FK,self) │       │ asset_id (FK)       │
│ name                │       │ node_id (FK)        │
│ taxonomy_type       │       │ weight              │ ← NUMERIC(5,2)
│ level               │       │ created_at          │
│ created_at          │       └─────────────────────┘
│ updated_at          │
└─────────────────────┘

┌─────────────────────┐
│   simulations       │
├─────────────────────┤
│ id (PK, UUID)       │
│ type                │
│ status              │
│ parameters (JSONB)  │
│ result (JSONB)      │
│ created_at          │
│ completed_at        │
└─────────────────────┘
```

---

## 3. 테이블 정의

### 3.1 accounts

계좌 정보 테이블.

| Column | Type | Nullable | Default | Description |
|--------|------|:--------:|---------|-------------|
| id | UUID | NO | gen_random_uuid() | PK |
| name | VARCHAR(100) | NO | | 계좌명 |
| type | VARCHAR(20) | NO | | BROKERAGE, BANK, EXCHANGE, WALLET, OTHER |
| currency | VARCHAR(10) | NO | | 기준 통화 코드 |
| institution | VARCHAR(100) | YES | | 금융기관명 |
| created_at | TIMESTAMP | NO | now() | 생성일 |
| updated_at | TIMESTAMP | NO | now() | 수정일 |

**Indexes:**
| Name | Columns | Type |
|------|---------|------|
| accounts_pkey | id | PK |
| accounts_type_idx | type | INDEX |

---

### 3.2 assets

자산 정보 테이블.

| Column | Type | Nullable | Default | Description |
|--------|------|:--------:|---------|-------------|
| id | UUID | NO | gen_random_uuid() | PK |
| symbol | VARCHAR(20) | NO | | 종목 코드 (UNIQUE) |
| name | VARCHAR(200) | NO | | 자산명 |
| type | VARCHAR(20) | NO | | STOCK, CRYPTO, BOND, CASH, REAL_ESTATE, FUND, OTHER |
| currency | VARCHAR(10) | NO | | 기준 통화 코드 |
| exchange | VARCHAR(50) | YES | | 거래소 |
| created_at | TIMESTAMP | NO | now() | 생성일 |
| updated_at | TIMESTAMP | NO | now() | 수정일 |

**Indexes:**
| Name | Columns | Type |
|------|---------|------|
| assets_pkey | id | PK |
| assets_symbol_unique | symbol | UNIQUE |
| assets_type_idx | type | INDEX |

---

### 3.3 transactions

거래 내역 테이블.

| Column | Type | Nullable | Default | Description |
|--------|------|:--------:|---------|-------------|
| id | UUID | NO | gen_random_uuid() | PK |
| account_id | UUID | NO | | FK → accounts.id |
| asset_id | UUID | NO | | FK → assets.id |
| type | VARCHAR(20) | NO | | BUY, SELL, DIVIDEND, INTEREST, DEPOSIT, WITHDRAWAL |
| quantity | NUMERIC(20,8) | NO | | 수량 |
| price | NUMERIC(20,8) | NO | | 단가 |
| fee | NUMERIC(20,8) | NO | 0 | 수수료 |
| currency | VARCHAR(10) | NO | | 거래 통화 |
| transaction_date | DATE | NO | | 거래일 |
| note | TEXT | YES | | 메모 |
| created_at | TIMESTAMP | NO | now() | 생성일 |
| updated_at | TIMESTAMP | NO | now() | 수정일 |

**Indexes:**
| Name | Columns | Type |
|------|---------|------|
| transactions_pkey | id | PK |
| transactions_account_idx | account_id | INDEX |
| transactions_asset_idx | asset_id | INDEX |
| transactions_date_idx | transaction_date | INDEX |
| transactions_type_idx | type | INDEX |

**Foreign Keys:**
| Column | References | On Delete | On Update |
|--------|------------|-----------|-----------|
| account_id | accounts.id | CASCADE | CASCADE |
| asset_id | assets.id | CASCADE | CASCADE |

---

### 3.4 daily_prices

일별 시세 테이블.

| Column | Type | Nullable | Default | Description |
|--------|------|:--------:|---------|-------------|
| id | UUID | NO | gen_random_uuid() | PK |
| asset_id | UUID | NO | | FK → assets.id |
| date | DATE | NO | | 날짜 |
| open | NUMERIC(20,8) | YES | | 시가 |
| high | NUMERIC(20,8) | YES | | 고가 |
| low | NUMERIC(20,8) | YES | | 저가 |
| close | NUMERIC(20,8) | NO | | 종가 |
| volume | BIGINT | YES | | 거래량 |
| source | VARCHAR(20) | NO | | YAHOO, COINGECKO, MANUAL |
| created_at | TIMESTAMP | NO | now() | 생성일 |

**Indexes:**
| Name | Columns | Type |
|------|---------|------|
| daily_prices_pkey | id | PK |
| daily_prices_asset_date_unique | asset_id, date | UNIQUE |

---

### 3.5 exchange_rates

환율 테이블.

| Column | Type | Nullable | Default | Description |
|--------|------|:--------:|---------|-------------|
| id | UUID | NO | gen_random_uuid() | PK |
| from_currency | VARCHAR(10) | NO | | 원본 통화 |
| to_currency | VARCHAR(10) | NO | | 대상 통화 |
| rate | NUMERIC(20,10) | NO | | 환율 |
| date | DATE | NO | | 날짜 |
| source | VARCHAR(20) | NO | | ECB, MANUAL |
| created_at | TIMESTAMP | NO | now() | 생성일 |

**Indexes:**
| Name | Columns | Type |
|------|---------|------|
| exchange_rates_pkey | id | PK |
| exchange_rates_pair_date_unique | from_currency, to_currency, date | UNIQUE |

---

### 3.6 taxonomy_nodes

분류 체계 트리 노드.

| Column | Type | Nullable | Default | Description |
|--------|------|:--------:|---------|-------------|
| id | UUID | NO | gen_random_uuid() | PK |
| parent_id | UUID | YES | | FK → taxonomy_nodes.id (self-ref) |
| name | VARCHAR(100) | NO | | 노드명 |
| taxonomy_type | VARCHAR(20) | NO | | ASSET_TYPE, REGION, INDUSTRY, CURRENCY |
| level | INTEGER | NO | 0 | 트리 깊이 |
| created_at | TIMESTAMP | NO | now() | 생성일 |
| updated_at | TIMESTAMP | NO | now() | 수정일 |

---

### 3.7 asset_classifications

자산-분류 매핑 (다대다, 가중치 포함).

| Column | Type | Nullable | Default | Description |
|--------|------|:--------:|---------|-------------|
| id | UUID | NO | gen_random_uuid() | PK |
| asset_id | UUID | NO | | FK → assets.id |
| node_id | UUID | NO | | FK → taxonomy_nodes.id |
| weight | NUMERIC(5,2) | NO | 100.00 | 가중치 (%) |
| created_at | TIMESTAMP | NO | now() | 생성일 |

---

### 3.8 simulations

시뮬레이션 실행 기록.

| Column | Type | Nullable | Default | Description |
|--------|------|:--------:|---------|-------------|
| id | UUID | NO | gen_random_uuid() | PK |
| type | VARCHAR(20) | NO | | MONTE_CARLO, SCENARIO |
| status | VARCHAR(20) | NO | PENDING | PENDING, RUNNING, COMPLETED, FAILED |
| parameters | JSONB | NO | | 입력 파라미터 |
| result | JSONB | YES | | 실행 결과 |
| created_at | TIMESTAMP | NO | now() | 생성일 |
| completed_at | TIMESTAMP | YES | | 완료일 |

---

## 4. ENUM 정의

### 4.1 account_type
| Value | Description |
|-------|-------------|
| BROKERAGE | 증권 계좌 |
| BANK | 은행 계좌 |
| EXCHANGE | 거래소 (크립토) |
| WALLET | 지갑 (크립토) |
| OTHER | 기타 |

### 4.2 asset_type
| Value | Description |
|-------|-------------|
| STOCK | 주식 |
| CRYPTO | 암호화폐 |
| BOND | 채권 |
| CASH | 현금/예금 |
| REAL_ESTATE | 부동산 |
| FUND | 펀드/ETF |
| OTHER | 기타 |

### 4.3 transaction_type
| Value | Description |
|-------|-------------|
| BUY | 매수 |
| SELL | 매도 |
| DIVIDEND | 배당 |
| INTEREST | 이자 |
| DEPOSIT | 입금 |
| WITHDRAWAL | 출금 |

### 4.4 taxonomy_type
| Value | Description |
|-------|-------------|
| ASSET_TYPE | 자산 유형별 |
| REGION | 지역별 |
| INDUSTRY | 산업별 |
| CURRENCY | 통화별 |

---

## 5. 변경 이력

| 버전 | 날짜 | 변경 내용 | 작성자 |
|-----|-----|----------|-------|
| 1.0 | 2026-02-11 | 최초 작성 | ericson |
