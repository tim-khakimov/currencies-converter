package com.timkhakimov.currenciesconverter.presentation

import com.timkhakimov.currenciesconverter.presentation.model.ConverterViewModelState
import com.timkhakimov.currenciesconverter.presentation.model.CurrencyValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ConverterViewModelReducer {

    private val _viewModelState = MutableStateFlow(ConverterViewModelState())

    val viewModelState = _viewModelState.asStateFlow()

    fun setLoading(isLoading: Boolean) {
        _viewModelState.update {
            it.copy(
                isLoading = isLoading,
                isCurrenciesLoadingError = false,
                isRatesLoadingError = false,
            )
        }
    }

    fun setCurrenciesError() {
        _viewModelState.update {
            it.copy(
                isLoading = false,
                isCurrenciesLoadingError = true
            )
        }
    }

    fun setRatesError() {
        _viewModelState.update {
            it.copy(
                isLoading = false,
                isRatesLoadingError = true
            )
        }
    }

    fun setInputValue(inputValue: Double?) {
        _viewModelState.update {
            it.copy(
                inputValue = inputValue,
                isLoading = inputValue != null,
                isCurrenciesLoadingError = false,
                isRatesLoadingError = false,
                currencyValues = listOf(),
            )
        }
    }

    fun setTargetCurrency(targetCurrency: String) {
        _viewModelState.update {
            it.copy(
                targetCurrency = targetCurrency,
                isCurrenciesLoadingError = false,
                isRatesLoadingError = false,
                isLoading = it.inputValue != null,
                currencyValues = listOf(),
            )
        }
    }

    fun setCurrencyValues(currencyValues: List<CurrencyValue>) {
        _viewModelState.update {
            it.copy(
                isLoading = false,
                isCurrenciesLoadingError = false,
                isRatesLoadingError = false,
                isFirstLoaded = true,
                currencyValues = currencyValues
            )
        }
    }
}
