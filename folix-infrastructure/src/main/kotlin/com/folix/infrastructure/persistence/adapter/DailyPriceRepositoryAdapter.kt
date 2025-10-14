package com.folix.infrastructure.persistence.adapter

import com.folix.application.port.out.DailyPriceRepository
import com.folix.infrastructure.persistence.entity.DailyPriceEntity
import com.folix.infrastructure.persistence.repository.JpaDailyPriceRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

/**
 * [DailyPriceRepository] 인터페이스 구현체.
 *
 * JPA를 통해 일별 시세 데이터의 영속성을 담당한다.
 */
@Repository
class DailyPriceRepositoryAdapter(
    private val jpaRepository: JpaDailyPriceRepository
) : DailyPriceRepository {

    override fun savePrice(assetId: UUID, date: LocalDate, close: BigDecimal, source: String) {
        val entity = DailyPriceEntity(
            assetId = assetId,
            date = date,
            close = close,
            source = source
        )
        jpaRepository.save(entity)
    }

    override fun findLatestPrice(assetId: UUID): BigDecimal? {
        return jpaRepository.findLatestByAssetId(assetId)?.close
    }

    override fun findPriceAt(assetId: UUID, date: LocalDate): BigDecimal? {
        return jpaRepository.findByAssetIdAndDate(assetId, date)?.close
    }

    override fun findPriceRange(
        assetId: UUID,
        from: LocalDate,
        to: LocalDate
    ): List<Pair<LocalDate, BigDecimal>> {
        return jpaRepository.findByAssetIdAndDateRange(assetId, from, to)
            .map { it.date to it.close }
    }
}
