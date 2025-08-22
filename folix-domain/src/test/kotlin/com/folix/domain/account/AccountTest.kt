package com.folix.domain.account

import com.folix.domain.money.Currency
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class AccountTest : DescribeSpec({

    describe("Account") {

        describe("생성") {
            it("should_create_with_valid_params") {
                val account = Account(
                    name = "키움증권",
                    type = AccountType.BROKERAGE,
                    currency = Currency.KRW,
                    institution = "키움증권"
                )
                account.name shouldBe "키움증권"
                account.type shouldBe AccountType.BROKERAGE
            }

            it("should_fail_when_name_is_blank") {
                shouldThrow<IllegalArgumentException> {
                    Account(name = "", type = AccountType.BANK, currency = Currency.KRW)
                }
            }
        }

        describe("팩토리 메서드") {
            it("should_create_brokerage") {
                val account = Account.brokerage("키움증권", Currency.KRW, "키움증권")
                account.type shouldBe AccountType.BROKERAGE
                account.institution shouldBe "키움증권"
            }

            it("should_create_exchange") {
                val account = Account.exchange("Binance")
                account.type shouldBe AccountType.EXCHANGE
                account.currency shouldBe Currency.USD
                account.institution shouldBe "Binance"
            }
        }

        describe("toString") {
            it("should_format_name_and_type") {
                val account = Account.brokerage("Test", Currency.USD)
                account.toString() shouldBe "Test (BROKERAGE)"
            }
        }
    }
})
