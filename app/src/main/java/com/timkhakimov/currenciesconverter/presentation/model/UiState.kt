package com.timkhakimov.currenciesconverter.presentation.model

sealed interface UiState {

    val targetCurrency: String

    data class Initial(
        override val targetCurrency: String
    ) : UiState

    data class Loading(
        override val targetCurrency: String
    ) : UiState

    data class Error(
        override val targetCurrency: String
    ) : UiState

    data class ContentLoaded(
        override val targetCurrency: String,
        val currencyValues: List<CurrencyValue>
    ) : UiState
}
