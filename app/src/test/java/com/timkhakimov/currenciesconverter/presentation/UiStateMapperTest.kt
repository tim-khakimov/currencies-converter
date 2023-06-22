package com.timkhakimov.currenciesconverter.presentation

import com.timkhakimov.currenciesconverter.BaseTestCase
import com.timkhakimov.currenciesconverter.data.Constants.BASE_CURRENCY
import com.timkhakimov.currenciesconverter.data.persistence.model.Currency
import com.timkhakimov.currenciesconverter.presentation.model.ConverterViewModelState
import com.timkhakimov.currenciesconverter.presentation.model.CurrencyValue
import com.timkhakimov.currenciesconverter.presentation.model.UiState
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UiStateMapperTest : BaseTestCase() {

    private lateinit var mapper: UiStateMapper

    override fun initBeforeEachTest() {
        super.initBeforeEachTest()
        mapper = UiStateMapper()
    }

    @Test
    fun `should be Initial by default`() {
        val viewModelState = ConverterViewModelState(BASE_CURRENCY)

        val uiState = mapper.map(viewModelState)

        val expectedUiState = UiState.Initial(BASE_CURRENCY)

        assertEquals(expectedUiState, uiState)
    }

    @Test
    fun `should be Loading if !isFirstLoaded && isLoading`() {
        val viewModelState = ConverterViewModelState(
            targetCurrency = TARGET_CURRENCY,
            isFirstLoaded = false,
            isLoading = true
        )

        val uiState = mapper.map(viewModelState)

        val expectedUiState = UiState.Loading(TARGET_CURRENCY)

        assertEquals(expectedUiState, uiState)
    }

    @Test
    fun `should be error if currencies error`() {
        val viewModelState = ConverterViewModelState(
            targetCurrency = TARGET_CURRENCY,
            isCurrenciesLoadingError = true
        )

        val uiState = mapper.map(viewModelState)

        val expectedUiState = UiState.Error(TARGET_CURRENCY)

        assertEquals(expectedUiState, uiState)
    }

    @Test
    fun `should be error if rates error`() {
        val viewModelState = ConverterViewModelState(
            targetCurrency = TARGET_CURRENCY,
            isRatesLoadingError = true
        )

        val uiState = mapper.map(viewModelState)

        val expectedUiState = UiState.Error(TARGET_CURRENCY)

        assertEquals(expectedUiState, uiState)
    }

    @Test
    fun `should show content if has currency values without error and loading`() {
        val viewModelState = ConverterViewModelState(
            targetCurrency = TARGET_CURRENCY,
            currencyValues = CURRENCY_VALUES,
            isFirstLoaded = true,
        )

        val uiState = mapper.map(viewModelState)

        val expectedUiState = UiState.ContentLoaded(
            targetCurrency = TARGET_CURRENCY,
            currencyValues = CURRENCY_VALUES,
        )

        assertEquals(expectedUiState, uiState)
    }

    @Test
    fun `should not show content if has currency values with error`() {
        val viewModelState = ConverterViewModelState(
            targetCurrency = TARGET_CURRENCY,
            currencyValues = CURRENCY_VALUES,
            isCurrenciesLoadingError = true
        )

        val uiState = mapper.map(viewModelState)

        val expectedUiState = UiState.Error(TARGET_CURRENCY)

        assertEquals(expectedUiState, uiState)
    }

    @Test
    fun `should not show content if has currency values with loading`() {
        val viewModelState = ConverterViewModelState(
            targetCurrency = TARGET_CURRENCY,
            currencyValues = CURRENCY_VALUES,
            isLoading = true,
        )

        val uiState = mapper.map(viewModelState)

        val expectedUiState = UiState.Loading(TARGET_CURRENCY)

        assertEquals(expectedUiState, uiState)
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