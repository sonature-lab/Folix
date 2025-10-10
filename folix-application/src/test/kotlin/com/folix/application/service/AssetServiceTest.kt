package com.folix.application.service

import com.folix.application.exception.DuplicateEntityException
import com.folix.application.exception.EntityNotFoundException
import com.folix.application.port.`in`.AssetUseCase.CreateAssetCommand
import com.folix.application.port.out.AssetRepository
import com.folix.domain.asset.Asset
import com.folix.domain.asset.AssetClass
import com.folix.domain.asset.AssetType
import com.folix.domain.money.Currency
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class AssetServiceTest : DescribeSpec({

    val assetRepository = mockk<AssetRepository>()
    val assetService = AssetService(assetRepository)

    val apple = Asset.stock("AAPL", "Apple Inc.", exchange = "NASDAQ")

    describe("createAsset") {
        it("should_create_asset_when_symbol_not_exists") {
            val command = CreateAssetCommand(
                symbol = "AAPL", name = "Apple Inc.",
                type = AssetType.STOCK, currency = "USD", exchange = "NASDAQ"
            )
            every { assetRepository.existsBySymbol("AAPL") } returns false
            every { assetRepository.save(any()) } answers { firstArg() }

            val result = assetService.createAsset(command)

            result.symbol shouldBe "AAPL"
            result.type shouldBe AssetType.STOCK
            result.assetClass shouldBe AssetClass.EQUITY
            verify { assetRepository.save(any()) }
        }

        it("should_throw_when_symbol_already_exists") {
            val command = CreateAssetCommand(
                symbol = "AAPL", name = "Apple",
                type = AssetType.STOCK, currency = "USD"
            )
            every { assetRepository.existsBySymbol("AAPL") } returns true

            shouldThrow<DuplicateEntityException> {
                assetService.createAsset(command)
            }
        }

        it("should_resolve_asset_class_for_each_type") {
            every { assetRepository.existsBySymbol(any()) } returns false
            every { assetRepository.save(any()) } answers { firstArg() }

            val cryptoCmd = CreateAssetCommand("BTC", "Bitcoin", AssetType.CRYPTO, "USD")
            assetService.createAsset(cryptoCmd).assetClass shouldBe AssetClass.CRYPTOCURRENCY

            val bondCmd = CreateAssetCommand("UST", "US Treasury", AssetType.BOND, "USD")
            assetService.createAsset(bondCmd).assetClass shouldBe AssetClass.FIXED_INCOME

            val cashCmd = CreateAssetCommand("USD", "US Dollar", AssetType.CASH, "USD")
            assetService.createAsset(cashCmd).assetClass shouldBe AssetClass.CASH_EQUIVALENT
        }
    }

    describe("getAsset") {
        it("should_return_asset_when_found") {
            every { assetRepository.findById(apple.id) } returns apple

            val result = assetService.getAsset(apple.id)
            result.symbol shouldBe "AAPL"
        }

        it("should_throw_when_not_found") {
            val id = UUID.randomUUID()
            every { assetRepository.findById(id) } returns null

            shouldThrow<EntityNotFoundException> {
                assetService.getAsset(id)
            }
        }
    }

    describe("getAllAssets") {
        it("should_return_all_assets") {
            every { assetRepository.findAll() } returns listOf(apple)

            val result = assetService.getAllAssets()
            result.size shouldBe 1
        }
    }

    describe("getAssetsByType") {
        it("should_filter_by_type") {
            every { assetRepository.findByType(AssetType.STOCK) } returns listOf(apple)

            val result = assetService.getAssetsByType(AssetType.STOCK)
            result.size shouldBe 1
            result.first().type shouldBe AssetType.STOCK
        }
    }
})
