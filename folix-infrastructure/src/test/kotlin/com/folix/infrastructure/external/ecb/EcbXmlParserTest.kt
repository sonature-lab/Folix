package com.folix.infrastructure.external.ecb

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal
import java.time.LocalDate

class EcbXmlParserTest : DescribeSpec({

    describe("parseDailyRates") {
        it("should parse daily rates XML correctly") {
            val xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <gesmes:Envelope xmlns:gesmes="http://www.gesmes.org/xml/2002-08-01" xmlns="http://www.ecb.int/vocabulary/2002-08-01/eurofxref">
                    <Cube>
                        <Cube time="2026-02-11">
                            <Cube currency="USD" rate="1.0800"/>
                            <Cube currency="JPY" rate="160.25"/>
                            <Cube currency="GBP" rate="0.8500"/>
                        </Cube>
                    </Cube>
                </gesmes:Envelope>
            """.trimIndent()

            val rates = EcbXmlParser.parseDailyRates(xml)

            rates shouldNotBe null
            rates.size shouldBe 3
            rates["USD"] shouldBe BigDecimal("1.0800")
            rates["JPY"] shouldBe BigDecimal("160.25")
            rates["GBP"] shouldBe BigDecimal("0.8500")
        }
    }

    describe("parseHistoricalRates") {
        it("should parse historical rates XML correctly") {
            val xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <gesmes:Envelope xmlns:gesmes="http://www.gesmes.org/xml/2002-08-01" xmlns="http://www.ecb.int/vocabulary/2002-08-01/eurofxref">
                    <Cube>
                        <Cube time="2026-02-11">
                            <Cube currency="USD" rate="1.0800"/>
                            <Cube currency="JPY" rate="160.25"/>
                        </Cube>
                        <Cube time="2026-02-10">
                            <Cube currency="USD" rate="1.0750"/>
                            <Cube currency="JPY" rate="159.80"/>
                        </Cube>
                    </Cube>
                </gesmes:Envelope>
            """.trimIndent()

            val historicalRates = EcbXmlParser.parseHistoricalRates(xml)

            historicalRates shouldNotBe null
            historicalRates.size shouldBe 2

            val feb11Rates = historicalRates[LocalDate.of(2026, 2, 11)]
            feb11Rates shouldNotBe null
            feb11Rates!!["USD"] shouldBe BigDecimal("1.0800")
            feb11Rates["JPY"] shouldBe BigDecimal("160.25")

            val feb10Rates = historicalRates[LocalDate.of(2026, 2, 10)]
            feb10Rates shouldNotBe null
            feb10Rates!!["USD"] shouldBe BigDecimal("1.0750")
            feb10Rates["JPY"] shouldBe BigDecimal("159.80")
        }
    }
})
