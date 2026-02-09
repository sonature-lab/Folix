# Folix

**[한국어](README.ko.md)**

> Multi-currency portfolio modeling engine — Kotlin + Spring Boot

Folix is a backend engine for personal portfolio management, providing multi-currency support, performance calculation (TWR/IRR), risk analysis, and Monte Carlo simulation. Built with **Clean Architecture** and **Domain-Driven Design** principles in pure Kotlin.

---

## Architecture

Clean Architecture multi-module with strict dependency direction:

```
folix-api  →  folix-application  →  folix-domain
                     ↑
            folix-infrastructure
```

| Module | Responsibility |
|--------|---------------|
| **folix-domain** | Pure domain models, financial calculations (zero framework dependencies) |
| **folix-application** | Use cases, inbound/outbound ports |
| **folix-infrastructure** | JPA entities, repository adapters, external API clients |
| **folix-api** | REST controllers, DTOs, Spring Boot configuration |

---

## Domain Packages

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

## Key Features

- **Money DSL** — Type-safe monetary operations with `BigDecimal` precision
- **Multi-currency** — Wallet with exchange rate conversion
- **Portfolio Analysis** — Position tracking, asset allocation by type/region/currency
- **Performance Engine** — Time-Weighted Return (TWR), Internal Rate of Return (IRR)
- **Risk Metrics** — Sharpe ratio, Sortino ratio, max drawdown, volatility
- **Monte Carlo Simulation** — Configurable scenarios with confidence intervals
- **Taxonomy** — Hierarchical asset classification system

---

## Tech Stack

| Area | Choice |
|------|--------|
| Language | Kotlin 2.1 |
| Framework | Spring Boot 3.4 |
| Build | Gradle Kotlin DSL |
| Persistence | Spring Data JPA + PostgreSQL 16 |
| Cache | Redis 7 |
| API | REST + OpenAPI 3.0 (Swagger UI) |
| Test | Kotest + MockK + JUnit 5 |
| Container | Docker + Docker Compose |
| JDK | 21 |

---

## Getting Started

### Prerequisites

- JDK 21+
- Docker & Docker Compose

### 1. Clone & start infrastructure

```bash
git clone https://github.com/sonature-lab/Folix.git
cd Folix
docker compose up -d
```

### 2. Run the application

```bash
./gradlew :folix-api:bootRun --args='--spring.profiles.active=local'
```

### 3. Access Swagger UI

```
http://localhost:8080/swagger-ui.html
```

### 4. Run tests

```bash
./gradlew test
```

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/assets` | Register a new asset |
| `GET` | `/api/v1/assets` | List all assets (filter by `?type=`) |
| `GET` | `/api/v1/assets/{id}` | Get asset by ID |
| `POST` | `/api/v1/accounts` | Create an account |
| `GET` | `/api/v1/accounts` | List all accounts |
| `GET` | `/api/v1/accounts/{id}` | Get account by ID |
| `POST` | `/api/v1/transactions` | Record a transaction |
| `GET` | `/api/v1/transactions` | List transactions (filter by account/asset/date) |
| `GET` | `/api/v1/transactions/{id}` | Get transaction by ID |
| `GET` | `/api/v1/portfolio` | Portfolio summary |
| `GET` | `/api/v1/portfolio/positions` | Current positions |
| `GET` | `/api/v1/portfolio/allocation` | Asset allocation |
| `GET` | `/api/v1/portfolio/performance` | Performance (TWR/IRR) |
| `GET` | `/api/v1/health` | Health check |

---

## Project Structure

```
Folix/
├── folix-domain/              # Pure domain models & financial logic
├── folix-application/         # Use cases & port interfaces
├── folix-infrastructure/      # JPA adapters & external integrations
├── folix-api/                 # REST API & Spring Boot app
├── docker-compose.yml         # PostgreSQL + Redis
├── Dockerfile                 # Production container image
├── build.gradle.kts           # Root build configuration
└── settings.gradle.kts        # Multi-module settings
```

---

## Development Roadmap

| Phase | Goal | Status |
|-------|------|--------|
| Phase 1 | Core domain (money, asset, account, transaction, portfolio, taxonomy) | Done |
| Phase 2 | Performance engine (TWR/IRR, Monte Carlo, risk metrics) | Done |
| Phase 3 | API + Infrastructure (Spring Boot, JPA, Docker) | Done |
| Phase 4 | External integrations (Yahoo Finance, CoinGecko, ECB rates) | Planned |
| Phase 5 | Deployment + Monitoring (Prometheus, Grafana, OCI) | Planned |

---

## License

This project is for personal portfolio and educational purposes.
