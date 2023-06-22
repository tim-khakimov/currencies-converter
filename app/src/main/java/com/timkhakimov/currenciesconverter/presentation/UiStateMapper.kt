package com.timkhakimov.currenciesconverter.presentation

import com.timkhakimov.currenciesconverter.presentation.model.ConverterViewModelState
import com.timkhakimov.currenciesconverter.presentation.model.UiState

class UiStateMapper {

    fun map(viewModelState: ConverterViewModelState): UiState {
        return when {
            viewModelState.isLoading -> {
                UiState.Loading(viewModelState.targetCurrency)
            }
            viewModelState.isCurrenciesLoadingError || viewModelState.isRatesLoadingError -> {
                UiState.Error(viewModelState.targetCurrency)
            }
            !viewModelState.isFirstLoaded && viewModelState.inputValue == null -> {
                UiState.Initial(viewModelState.targetCurrency)
            }
            else -> {
                UiState.ContentLoaded(
                    viewModelState.targetCurrency,
                    viewModelState.currencyValues
                )
            }
        }
    }
}