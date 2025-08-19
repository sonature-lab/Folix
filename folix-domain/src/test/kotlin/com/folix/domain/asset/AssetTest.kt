package com.folix.domain.asset

import com.folix.domain.money.Currency
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class AssetTest : DescribeSpec({

    describe("Asset") {

        describe("생성") {
            it("should_create_with_valid_params") {
                val asset = Asset(
                    symbol = "AAPL",
                    name = "Apple Inc.",
                    type = AssetType.STOCK,
                    assetClass = AssetClass.EQUITY,
                    currency = Currency.USD,
                    exchange = "NASDAQ"
                )
                asset.symbol shouldBe "AAPL"
                asset.type shouldBe AssetType.STOCK
                asset.exchange shouldBe "NASDAQ"
            }

            it("should_fail_when_symbol_is_blank") {
                shouldThrow<IllegalArgumentException> {
                    Asset(symbol = "", name = "Test", type = AssetType.STOCK,
                        assetClass = AssetClass.EQUITY, currency = Currency.USD)
                }
            }

            it("should_fail_when_name_is_blank") {
                shouldThrow<IllegalArgumentException> {
                    Asset(symbol = "TEST", name = "", type = AssetType.STOCK,
                        assetClass = AssetClass.EQUITY, currency = Currency.USD)
                }
            }
        }

        describe("팩토리 메서드") {
            it("should_create_stock") {
                val apple = Asset.stock("AAPL", "Apple Inc.", exchange = "NASDAQ")
                apple.type shouldBe AssetType.STOCK
                apple.assetClass shouldBe AssetClass.EQUITY
                apple.currency shouldBe Currency.USD
            }

            it("should_create_crypto") {
                val btc = Asset.crypto("BTC", "Bitcoin")
                btc.type shouldBe AssetType.CRYPTO
                btc.assetClass shouldBe AssetClass.CRYPTOCURRENCY
            }

            it("should_create_cash") {
                val cash = Asset.cash(Currency.KRW)
                cash.symbol shouldBe "KRW"
                cash.type shouldBe AssetType.CASH
                cash.assetClass shouldBe AssetClass.CASH_EQUIVALENT
            }
        }

        describe("고유 ID") {
            it("should_generate_unique_id") {
                val a = Asset.stock("AAPL", "Apple")
                val b = Asset.stock("AAPL", "Apple")
                a.id shouldNotBe b.id
            }
        }
    }
})
