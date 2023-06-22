package com.timkhakimov.currenciesconverter.data.repository

import com.timkhakimov.currenciesconverter.BaseTestCase
import com.timkhakimov.currenciesconverter.data.Constants.APP_ID
import com.timkhakimov.currenciesconverter.data.Constants.BASE_CURRENCY
import com.timkhakimov.currenciesconverter.data.persistence.dao.RatesDao
import com.timkhakimov.currenciesconverter.data.persistence.model.Rate
import com.timkhakimov.currenciesconverter.data.persistence.model.SavedRates
import com.timkhakimov.currenciesconverter.data.rest.ExchangeRatesRestService
import com.timkhakimov.currenciesconverter.data.rest.model.LatestRates
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class RatesRepositoryImplTest : BaseTestCase() {

    @MockK
    private lateinit var exchangeRatesRestService: ExchangeRatesRestService

    @MockK
    private lateinit var ratesDao: RatesDao

    private lateinit var ratesRepositoryImpl: RatesRepositoryImpl

    override fun initBeforeEachTest() {
        ratesRepositoryImpl = RatesRepositoryImpl(exchangeRatesRestService, ratesDao)
    }

    @Test
    fun `should get rates from rest service if there is no saved info`() = runTest {
        coEvery {
            exchangeRatesRestService.getLatestRates(any(), any())
        } returns LatestRates(
            BASE_CURRENCY,
            RATES_MAP_FROM_REST
        )
        coEvery { ratesDao.getSavedRates(any()) } returns null
        coEvery { ratesDao.saveRates(any(), any()) } returns Unit

        val rates = ratesRepositoryImpl.getRates()

        val expectedRates = listOf(
            Rate(BASE_CURRENCY, "EUR", 0.93),
            Rate(BASE_CURRENCY, "GBP", 0.79),
            Rate(BASE_CURRENCY, "JPY", 139.4),
            Rate(BASE_CURRENCY, "USD", 1.0),
        )

        coVerifySequence {
            ratesDao.getSavedRates(BASE_CURRENCY)
            exchangeRatesRestService.getLatestRates(APP_ID, BASE_CURRENCY)
            ratesDao.saveRates(any(), expectedRates)
        }

        assertEquals(rates.rates, expectedRates)
    }

    @Test
    fun `should get rates from rest service if saved info is not actual`() = runTest {
        coEvery {
            exchangeRatesRestService.getLatestRates(any(), any())
        } returns LatestRates(
            BASE_CURRENCY,
            RATES_MAP_FROM_REST
        )

        val timestamp = System.currentTimeMillis() - 1000 * 60 * 60
        coEvery {
            ratesDao.getSavedRates(any())
        } returns SavedRates(
            BASE_CURRENCY,
            timestamp,
            RATES_LIST_FROM_PERSISTENCE
        )

        coEvery { ratesDao.saveRates(any(), any()) } returns Unit

        val rates = ratesRepositoryImpl.getRates()

        val expectedRates = listOf(
            Rate(BASE_CURRENCY, "EUR", 0.93),
            Rate(BASE_CURRENCY, "GBP", 0.79),
            Rate(BASE_CURRENCY, "JPY", 139.4),
            Rate(BASE_CURRENCY, "USD", 1.0),
        )

        coVerifySequence {
            ratesDao.getSavedRates(BASE_CURRENCY)
            exchangeRatesRestService.getLatestRates(APP_ID, BASE_CURRENCY)
            ratesDao.saveRates(any(), expectedRates)
        }

        assertEquals(rates.rates, expectedRates)
    }

    @Test
    fun `should get rates from persistence if saved info is actual`() = runTest {
        coEvery {
            exchangeRatesRestService.getLatestRates(any(), any())
        } returns LatestRates(
            BASE_CURRENCY,
            RATES_MAP_FROM_REST
        )

        val timestamp = System.currentTimeMillis() - 1000 * 60 * 15
        coEvery {
            ratesDao.getSavedRates(any())
        } returns SavedRates(
            BASE_CURRENCY,
            timestamp,
            RATES_LIST_FROM_PERSISTENCE
        )

        coEvery { ratesDao.saveRates(any(), any()) } returns Unit

        val rates = ratesRepositoryImpl.getRates()

        val expectedRates = listOf(
            Rate(BASE_CURRENCY, "EUR", 0.95),
            Rate(BASE_CURRENCY, "GBP", 0.78),
            Rate(BASE_CURRENCY, "JPY", 138.1),
            Rate(BASE_CURRENCY, "USD", 1.0),
        )

        coVerify {
            ratesDao.getSavedRates(BASE_CURRENCY)
        }

        coVerify(inverse = true) {
            exchangeRatesRestService.getLatestRates(any(), any())
            ratesDao.saveRates(any(), any())
        }

        assertEquals(rates.rates, expectedRates)
    }

    private companion object {
        val RATES_MAP_FROM_REST = mapOf(
            "EUR" to 0.93,
            "GBP" to 0.79,
            "JPY" to 139.4,
            "USD" to 1.0
        )

        val RATES_LIST_FROM_PERSISTENCE = listOf<Rate>(
            Rate(BASE_CURRENCY, "EUR", 0.95),
            Rate(BASE_CURRENCY, "GBP", 0.78),
            Rate(BASE_CURRENCY, "JPY", 138.1),
            Rate(BASE_CURRENCY, "USD", 1.0),
        )
    }
}