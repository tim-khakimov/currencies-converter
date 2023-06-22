package com.timkhakimov.currenciesconverter.presentation.model

import com.timkhakimov.currenciesconverter.data.persistence.model.Currency

data class CurrencyValue(
    val currency: Currency,
    val rate: Double?,
)
