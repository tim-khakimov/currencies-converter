package com.timkhakimov.currenciesconverter.data.repository

import com.timkhakimov.currenciesconverter.BaseTestCase
import com.timkhakimov.currenciesconverter.data.Constants.CURRENCIES_INFO_ID
import com.timkhakimov.currenciesconverter.data.persistence.dao.CurrenciesDao
import com.timkhakimov.currenciesconverter.data.persistence.model.CurrenciesInfo
import com.timkhakimov.currenciesconverter.data.persistence.model.Currency
import com.timkhakimov.currenciesconverter.data.rest.ExchangeRatesRestService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class CurrenciesRepositoryImplTest : BaseTestCase() {

    @MockK
    private lateinit var exchangeRatesRestService: ExchangeRatesRestService

    @MockK
    private lateinit var currenciesDao: CurrenciesDao

    private lateinit var currenciesRepositoryImpl: CurrenciesRepositoryImpl

    override fun initBeforeEachTest() {
        currenciesRepositoryImpl = CurrenciesRepositoryImpl(exchangeRatesRestService, currenciesDao)
    }

    @Test
    fun `should get currencies from rest service if there is no saved info`() = runTest {
        coEvery { exchangeRatesRestService.getCurrencies() } returns CURRENCIES_MAP_FROM_REST
        coEvery { currenciesDao.getCurrenciesInfo(any()) } returns null
        coEvery { currenciesDao.saveCurrencies(any(), any()) } returns Unit

        val currencies = currenciesRepositoryImpl.getCurrencies()

        val expectedCurrencies = listOf(
            Currency("EUR", "Euro"),
            Currency("USD", "Dollar"),
            Currency("GBP", "Pound"),
            Currency("JPY", "Yen"),
        )

        coVerifySequence {
            currenciesDao.getCurrenciesInfo(CURRENCIES_INFO_ID)
            exchangeRatesRestService.getCurrencies()
            currenciesDao.saveCurrencies(any(), expectedCurrencies)
        }

        assertEquals(expectedCurrencies, currencies)
    }

    @Test
    fun `should get currencies from rest service if saved info is not actual`() = runTest {
        coEvery { exchangeRatesRestService.getCurrencies() } returns CURRENCIES_MAP_FROM_REST

        val timestamp = System.currentTimeMillis() - 1000 * 60 * 60
        coEvery {
            currenciesDao.getCurrenciesInfo(any())
        } returns CurrenciesInfo(
            CURRENCIES_INFO_ID,
            timestamp
        )

        coEvery { currenciesDao.saveCurrencies(any(), any()) } returns Unit

        val currencies = currenciesRepositoryImpl.getCurrencies()

        val expectedCurrencies = listOf(
            Currency("EUR", "Euro"),
            Currency("USD", "Dollar"),
            Currency("GBP", "Pound"),
            Currency("JPY", "Yen"),
        )

        coVerifySequence {
            currenciesDao.getCurrenciesInfo(CURRENCIES_INFO_ID)
            exchangeRatesRestService.getCurrencies()
            currenciesDao.saveCurrencies(any(), expectedCurrencies)
        }

        assertEquals(expectedCurrencies, currencies)
    }


    @Test
    fun `should get currencies from persistence if saved info is actual`() = runTest {
        coEvery { exchangeRatesRestService.getCurrencies() } returns CURRENCIES_MAP_FROM_REST

        val timestamp = System.currentTimeMillis() - 1000 * 60 * 15
        coEvery {
            currenciesDao.getCurrenciesInfo(any())
        } returns CurrenciesInfo(
            CURRENCIES_INFO_ID,
            timestamp
        )

        coEvery { currenciesDao.getCurrencies() } returns CURRENCIES_LIST_FROM_PERSISTENCE

        val currencies = currenciesRepositoryImpl.getCurrencies()

        coVerifySequence {
            currenciesDao.getCurrenciesInfo(CURRENCIES_INFO_ID)
            currenciesDao.getCurrencies()
        }

        coVerify(inverse = true) {
            exchangeRatesRestService.getCurrencies()
            currenciesDao.saveCurrencies(any(), any())
        }

        assertEquals(CURRENCIES_LIST_FROM_PERSISTENCE, currencies)
    }

    private companion object {

        val CURRENCIES_MAP_FROM_REST = mapOf(
            "EUR" to "Euro",
            "USD" to "Dollar",
            "GBP" to "Pound",
            "JPY" to "Yen",
        )

        val CURRENCIES_LIST_FROM_PERSISTENCE = listOf<Currency>(
            Currency("EUR", "Euro"),
            Currency("USD", "Dollar"),
            Currency("GBP", "Pound",),
            Currency("JPY", "Yen"),
            Currency("CHF", "Franc"),
        )
    }
}