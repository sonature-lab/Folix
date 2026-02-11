package com.folix.api.config

import com.folix.application.port.out.AccountRepository
import com.folix.application.port.out.AssetRepository
import com.folix.application.port.out.DailyPriceRepository
import com.folix.application.port.out.ExchangeRateProvider
import com.folix.application.port.out.ExchangeRateRepository
import com.folix.application.port.out.MarketDataProvider
import com.folix.application.port.out.TransactionRepository
import com.folix.application.service.AccountService
import com.folix.application.service.AssetService
import com.folix.application.service.MarketDataService
import com.folix.application.service.PortfolioService
import com.folix.application.service.TransactionService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 애플리케이션 서비스 빈 설정.
 *
 * 애플리케이션 레이어의 서비스를 Spring Bean으로 등록한다.
 * 의존성은 인프라스트럭처 레이어에서 구현된 리포지토리를 주입받는다.
 */
@Configuration
class ServiceConfig {

    @Bean
    fun assetService(assetRepository: AssetRepository): AssetService {
        return AssetService(assetRepository)
    }

    @Bean
    fun accountService(accountRepository: AccountRepository): AccountService {
        return AccountService(accountRepository)
    }

    @Bean
    fun transactionService(
        transactionRepository: TransactionRepository,
        accountRepository: AccountRepository,
        assetRepository: AssetRepository
    ): TransactionService {
        return TransactionService(
            transactionRepository,
            accountRepository,
            assetRepository
        )
    }

    @Bean
    fun portfolioService(
        transactionRepository: TransactionRepository,
        assetRepository: AssetRepository,
        dailyPriceRepository: DailyPriceRepository
    ): PortfolioService {
        return PortfolioService(
            transactionRepository,
            assetRepository,
            dailyPriceRepository
        )
    }

    @Bean
    fun marketDataService(
        marketDataProviders: List<MarketDataProvider>,
        exchangeRateProvider: ExchangeRateProvider,
        dailyPriceRepository: DailyPriceRepository,
        exchangeRateRepository: ExchangeRateRepository,
        assetRepository: AssetRepository
    ): MarketDataService {
        return MarketDataService(
            marketDataProviders,
            exchangeRateProvider,
            dailyPriceRepository,
            exchangeRateRepository,
            assetRepository
        )
    }
}
