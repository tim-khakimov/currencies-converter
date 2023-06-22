package com.timkhakimov.currenciesconverter.presentation.model

import com.timkhakimov.currenciesconverter.data.Constants.BASE_CURRENCY

data class ConverterViewModelState(
    val targetCurrency: String = BASE_CURRENCY,
    val inputValue: Double? = null,
    val isFirstLoaded: Boolean = false,
    val isLoading: Boolean = false,
    val isCurrenciesLoadingError: Boolean = false,
    val isRatesLoadingError: Boolean = false,
    val currencyValues: List<CurrencyValue> = listOf()
)
