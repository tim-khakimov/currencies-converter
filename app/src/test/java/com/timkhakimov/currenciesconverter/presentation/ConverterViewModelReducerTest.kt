package com.timkhakimov.currenciesconverter.presentation

import com.timkhakimov.currenciesconverter.BaseTestCase
import com.timkhakimov.currenciesconverter.data.persistence.model.Currency
import com.timkhakimov.currenciesconverter.presentation.model.ConverterViewModelState
import com.timkhakimov.currenciesconverter.presentation.model.CurrencyValue
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class ConverterViewModelReducerTest : BaseTestCase() {

    private lateinit var converterViewModelReducer: ConverterViewModelReducer

    override fun initBeforeEachTest() {
        super.initBeforeEachTest()
        converterViewModelReducer = ConverterViewModelReducer()
    }

    @Test
    fun `loading should stop after set currency values`() = runTest {
        converterViewModelReducer.setLoading(true)
        val currencyValues = CURRENCY_VALUES
        converterViewModelReducer.setCurrencyValues(currencyValues)
        val expectedState = ConverterViewModelState(
            isLoading = false,
            isFirstLoaded = true,
            currencyValues = CURRENCY_VALUES,
        )
        assertEquals(expectedState, converterViewModelReducer.viewModelState.value)
    }

    @Test
    fun `loading should stop after set currencies error`() = runTest {
        converterViewModelReducer.setLoading(true)
        converterViewModelReducer.setCurrenciesError()
        val expectedState = ConverterViewModelState(
            isLoading = false,
            isCurrenciesLoadingError = true,
        )
        assertEquals(expectedState, converterViewModelReducer.viewModelState.value)
    }

    @Test
    fun `loading should stop after set rates error`() = runTest {
        converterViewModelReducer.setLoading(true)
        converterViewModelReducer.setRatesError()
        val expectedState = ConverterViewModelState(
            isLoading = false,
            isRatesLoadingError = true,
        )
        assertEquals(expectedState, converterViewModelReducer.viewModelState.value)
    }

    @Test
    fun `rates error should reset after start new loading`() = runTest {
        converterViewModelReducer.setLoading(true)
        converterViewModelReducer.setRatesError()
        converterViewModelReducer.setLoading(true)
        val expectedState = ConverterViewModelState(
            isLoading = true,
            isRatesLoadingError = false,
        )
        assertEquals(expectedState, converterViewModelReducer.viewModelState.value)
    }

    @Test
    fun `currencies error should reset after start new loading`() = runTest {
        converterViewModelReducer.setLoading(true)
        converterViewModelReducer.setCurrenciesError()
        converterViewModelReducer.setLoading(true)
        val expectedState = ConverterViewModelState(
            isLoading = true,
            isCurrenciesLoadingError = false,
        )
        assertEquals(expectedState, converterViewModelReducer.viewModelState.value)
    }

    @Test
    fun `first loaded value should be false if set error`() = runTest {
        converterViewModelReducer.setLoading(true)
        converterViewModelReducer.setCurrenciesError()
        val expectedState = ConverterViewModelState(
            isLoading = false,
            isFirstLoaded = false,
            isCurrenciesLoadingError = true,
        )
        assertEquals(expectedState, converterViewModelReducer.viewModelState.value)
    }

    @Test
    fun `currency values should clear after input new value`() = runTest {
        converterViewModelReducer.setCurrencyValues(CURRENCY_VALUES)
        converterViewModelReducer.setInputValue(INPUT_VALUE)
        val expectedState = ConverterViewModelState(
            inputValue = INPUT_VALUE,
            isLoading = true,
            isFirstLoaded = true,
            currencyValues = listOf(),
        )
        assertEquals(expectedState, converterViewModelReducer.viewModelState.value)
    }

    @Test
    fun `currency values should clear after select new target currency`() = runTest {
        converterViewModelReducer.setInputValue(INPUT_VALUE)
        converterViewModelReducer.setCurrencyValues(CURRENCY_VALUES)
        converterViewModelReducer.setTargetCurrency(TARGET_CURRENCY)
        val expectedState = ConverterViewModelState(
            inputValue = INPUT_VALUE,
            targetCurrency = TARGET_CURRENCY,
            isLoading = true,
            isFirstLoaded = true,
            currencyValues = listOf(),
        )
        assertEquals(expectedState, converterViewModelReducer.viewModelState.value)
    }

    @Test
    fun `loading should not start if input null value`() = runTest {
        converterViewModelReducer.setCurrencyValues(CURRENCY_VALUES)
        converterViewModelReducer.setInputValue(null)
        val expectedState = ConverterViewModelState(
            inputValue = null,
            isLoading = false,
            isFirstLoaded = true,
            currencyValues = listOf(),
        )
        assertEquals(expectedState, converterViewModelReducer.viewModelState.value)
    }

    @Test
    fun `loading should not start after new tarhet currency if input value is null`() = runTest {
        converterViewModelReducer.setTargetCurrency(TARGET_CURRENCY)
        val expectedState = ConverterViewModelState(
            targetCurrency = TARGET_CURRENCY,
            isLoading = false,
        )
        assertEquals(expectedState, converterViewModelReducer.viewModelState.value)
    }

    private companion object {
        const val TARGET_CURRENCY = "EUR"
        const val INPUT_VALUE = 1.0
        val CURRENCY_VALUES = listOf(
            CurrencyValue(Currency("EUR", "Euro"), 0.93),
            CurrencyValue(Currency("GBP", "Pound"), 0.79),
            CurrencyValue(Currency("USD", "Dollar"), 1.0),
            CurrencyValue(Currency("JPY", "Yen"), 139.4),
        )
    }
}