package com.folix.application.service

import com.folix.application.exception.DuplicateEntityException
import com.folix.application.exception.EntityNotFoundException
import com.folix.application.port.`in`.AssetUseCase
import com.folix.application.port.`in`.AssetUseCase.CreateAssetCommand
import com.folix.application.port.out.AssetRepository
import com.folix.domain.asset.Asset
import com.folix.domain.asset.AssetClass
import com.folix.domain.asset.AssetType
import com.folix.domain.money.Currency
import java.util.UUID

/**
 * 자산 관리 유즈케이스 구현.
 */
class AssetService(
    private val assetRepository: AssetRepository
) : AssetUseCase {

    override fun createAsset(command: CreateAssetCommand): Asset {
        if (assetRepository.existsBySymbol(command.symbol)) {
            throw DuplicateEntityException("Asset", "symbol", command.symbol)
        }

        val asset = Asset(
            symbol = command.symbol,
            name = command.name,
            type = command.type,
            assetClass = resolveAssetClass(command.type),
            currency = Currency.of(command.currency),
            exchange = command.exchange
        )

        return assetRepository.save(asset)
    }

    override fun getAsset(id: UUID): Asset =
        assetRepository.findById(id)
            ?: throw EntityNotFoundException("Asset", id)

    override fun getAllAssets(): List<Asset> =
        assetRepository.findAll()

    override fun getAssetsByType(type: AssetType): List<Asset> =
        assetRepository.findByType(type)

    private fun resolveAssetClass(type: AssetType): AssetClass = when (type) {
        AssetType.STOCK -> AssetClass.EQUITY
        AssetType.CRYPTO -> AssetClass.CRYPTOCURRENCY
        AssetType.BOND -> AssetClass.FIXED_INCOME
        AssetType.CASH -> AssetClass.CASH_EQUIVALENT
        AssetType.REAL_ESTATE -> AssetClass.REAL_ESTATE
        AssetType.FUND -> AssetClass.EQUITY
        AssetType.OTHER -> AssetClass.ALTERNATIVE
    }
}
