package com.folix.infrastructure.persistence.adapter

import com.folix.application.port.out.ExchangeRateRepository
import com.folix.domain.money.ExchangeRate
import com.folix.infrastructure.persistence.entity.ExchangeRateEntity
import com.folix.infrastructure.persistence.repository.JpaExchangeRateRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

/**
 * [ExchangeRateRepository] 인터페이스 구현체.
 *
 * JPA를 통해 ExchangeRate 도메인 객체의 영속성을 담당한다.
 */
@Repository
class ExchangeRateRepositoryAdapter(
    private val jpaRepository: JpaExchangeRateRepository
) : ExchangeRateRepository {

    override fun save(exchangeRate: ExchangeRate): ExchangeRate {
        val entity = ExchangeRateEntity.fromDomain(exchangeRate)
        val saved = jpaRepository.save(entity)
        return saved.toDomain()
    }

    override fun findRate(baseCurrency: String, quoteCurrency: String, date: LocalDate): ExchangeRate? {
        return jpaRepository.findByBaseCurrencyAndQuoteCurrencyAndDate(
            baseCurrency,
            quoteCurrency,
            date
        )?.toDomain()
    }

    override fun findLatestRate(baseCurrency: String, quoteCurrency: String): ExchangeRate? {
        return jpaRepository.findLatestByBaseCurrencyAndQuoteCurrency(
            baseCurrency,
            quoteCurrency
        )?.toDomain()
    }
}
