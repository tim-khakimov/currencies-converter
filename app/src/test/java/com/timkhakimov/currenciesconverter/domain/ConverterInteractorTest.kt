package com.timkhakimov.currenciesconverter.domain

import com.timkhakimov.currenciesconverter.BaseTestCase
import com.timkhakimov.currenciesconverter.data.Constants.BASE_CURRENCY
import com.timkhakimov.currenciesconverter.data.persistence.model.Rate
import com.timkhakimov.currenciesconverter.data.persistence.model.SavedRates
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class ConverterInteractorTest : BaseTestCase() {

    @MockK
    private lateinit var ratesRepository: RatesRepository

    private lateinit var converterInteractor: ConverterInteractor

    override fun initBeforeEachTest() {
        converterInteractor = ConverterInteractor(ratesRepository)
    }

    @Test
    fun `check base value for USD`() = runTest {
        coEvery {
            ratesRepository.getRates()
        } returns SavedRates(
            BASE_CURRENCY,
            123456L,
            RATES_LIST
        )

        val result = converterInteractor.getCurrenciesRates(BASE_CURRENCY, 1.0)

        val expectedCurrencyValues = mapOf(
            "EUR" to 0.95,
            "GBP" to 0.78,
            "JPY" to 138.1,
            "USD" to 1.0
        )

        assertCurrencyValuesMap(expectedCurrencyValues, result)
    }

    @Test
    fun `check big base value for USD`() = runTest {
        coEvery {
            ratesRepository.getRates()
        } returns SavedRates(
            BASE_CURRENCY,
            123456L,
            RATES_LIST
        )

        val result = converterInteractor.getCurrenciesRates(BASE_CURRENCY, 1000.0)

        val expectedCurrencyValues = mapOf(
            "EUR" to 950.0,
            "GBP" to 780.0,
            "JPY" to 138100.0,
            "USD" to 1000.0
        )

        assertCurrencyValuesMap(expectedCurrencyValues, result)
    }

    @Test
    fun `check other valid currency`() = runTest {
        coEvery {
            ratesRepository.getRates()
        } returns SavedRates(
            BASE_CURRENCY,
            123456L,
            RATES_LIST
        )

        val result = converterInteractor.getCurrenciesRates("EUR", 1.0)

        val expectedCurrencyValues = mapOf(
            "EUR" to 1.0,
            "GBP" to 0.821,     //0.78 / 0.95
            "JPY" to 145.368,   // 138.1 / 0.95,
            "USD" to 1.0526     //  1.0 / 0.95
        )

        assertCurrencyValuesMap(expectedCurrencyValues, result)
    }

    @Test
    fun `check zero with valid currency`() = runTest {
        coEvery {
            ratesRepository.getRates()
        } returns SavedRates(
            BASE_CURRENCY,
            123456L,
            RATES_LIST
        )

        val result = converterInteractor.getCurrenciesRates("EUR", 0.0)

        val expectedCurrencyValues = mapOf(
            "EUR" to 0.0,
            "GBP" to 0.0,
            "JPY" to 0.0,
            "USD" to 0.0
        )

        assertCurrencyValuesMap(expectedCurrencyValues, result)
    }

    @Test
    fun `check invalid value`() = runTest {
        coEvery {
            ratesRepository.getRates()
        } returns SavedRates(
            BASE_CURRENCY,
            123456L,
            RATES_LIST
        )

        val result = converterInteractor.getCurrenciesRates("VEF", 1.0)

        val expectedCurrencyValues = mapOf(
            "EUR" to null,
            "GBP" to null,
            "JPY" to null,
            "USD" to null
        )

        assertCurrencyValuesMap(expectedCurrencyValues, result)
    }

    @Test
    fun `check zero with invalid value`() = runTest {
        coEvery {
            ratesRepository.getRates()
        } returns SavedRates(
            BASE_CURRENCY,
            123456L,
            RATES_LIST
        )

        val result = converterInteractor.getCurrenciesRates("VEF", 1.0)

        val expectedCurrencyValues = mapOf(
            "EUR" to null,
            "GBP" to null,
            "JPY" to null,
            "USD" to null
        )

        assertCurrencyValuesMap(expectedCurrencyValues, result)
    }

    private fun assertCurrencyValuesMap(expected: Map<String, Double?>, actual: Map<String, Double?>) {
        assertEquals(expected.size, actual.size)
        expected.forEach { (key, value) ->
            if (value == null) {
                assertEquals(value, actual[key])
            } else {
                val roundedValue = Math.round(value * 1000)
                val roundedExpectedValue = Math.round(actual[key]!! * 1000)
                assertEquals(roundedValue, roundedExpectedValue)
            }
        }
    }

    private companion object {

        val RATES_LIST = listOf(
            Rate(BASE_CURRENCY, "EUR", 0.95),
            Rate(BASE_CURRENCY, "GBP", 0.78),
            Rate(BASE_CURRENCY, "JPY", 138.1),
            Rate(BASE_CURRENCY, "USD", 1.0),
        )
    }
}