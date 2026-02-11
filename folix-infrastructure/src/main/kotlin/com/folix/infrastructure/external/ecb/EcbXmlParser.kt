package com.folix.infrastructure.external.ecb

import org.w3c.dom.Element
import java.io.StringReader
import java.math.BigDecimal
import java.time.LocalDate
import javax.xml.parsers.DocumentBuilderFactory

/**
 * ECB XML 응답 파서.
 *
 * ECB의 eurofxref XML 형식을 파싱하여 환율 데이터를 추출한다.
 */
object EcbXmlParser {

    private const val CUBE_TAG = "Cube"
    private const val TIME_ATTR = "time"
    private const val CURRENCY_ATTR = "currency"
    private const val RATE_ATTR = "rate"

    /**
     * 일일 환율 XML을 파싱한다.
     *
     * @param xml eurofxref-daily.xml 내용
     * @return 통화 코드 → 환율(EUR 기준) 맵
     */
    fun parseDailyRates(xml: String): Map<String, BigDecimal> {
        val doc = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(org.xml.sax.InputSource(StringReader(xml)))

        val rateMap = mutableMapOf<String, BigDecimal>()

        val cubes = doc.getElementsByTagName(CUBE_TAG)
        for (i in 0 until cubes.length) {
            val cube = cubes.item(i) as? Element ?: continue

            if (cube.hasAttribute(CURRENCY_ATTR) && cube.hasAttribute(RATE_ATTR)) {
                val currency = cube.getAttribute(CURRENCY_ATTR)
                val rate = BigDecimal(cube.getAttribute(RATE_ATTR))
                rateMap[currency] = rate
            }
        }

        return rateMap
    }

    /**
     * 히스토리컬 환율 XML을 파싱한다.
     *
     * @param xml eurofxref-hist-90d.xml 내용
     * @return 날짜 → (통화 코드 → 환율) 맵
     */
    fun parseHistoricalRates(xml: String): Map<LocalDate, Map<String, BigDecimal>> {
        val doc = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(org.xml.sax.InputSource(StringReader(xml)))

        val historicalMap = mutableMapOf<LocalDate, MutableMap<String, BigDecimal>>()

        val cubes = doc.getElementsByTagName(CUBE_TAG)
        var currentDate: LocalDate? = null

        for (i in 0 until cubes.length) {
            val cube = cubes.item(i) as? Element ?: continue

            // 날짜 Cube
            if (cube.hasAttribute(TIME_ATTR)) {
                currentDate = LocalDate.parse(cube.getAttribute(TIME_ATTR))
                historicalMap[currentDate] = mutableMapOf()
            }

            // 환율 Cube
            if (currentDate != null &&
                cube.hasAttribute(CURRENCY_ATTR) &&
                cube.hasAttribute(RATE_ATTR)) {
                val currency = cube.getAttribute(CURRENCY_ATTR)
                val rate = BigDecimal(cube.getAttribute(RATE_ATTR))
                historicalMap[currentDate]?.put(currency, rate)
            }
        }

        return historicalMap
    }
}
