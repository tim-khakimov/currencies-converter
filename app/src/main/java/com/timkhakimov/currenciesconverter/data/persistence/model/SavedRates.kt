package com.timkhakimov.currenciesconverter.data.persistence.model

import androidx.room.Relation

data class SavedRates(
    val base: String,
    val timestamp: Long,
    @Relation(parentColumn = "base", entityColumn = "baseCurrencyCode")
    val rates: List<Rate>,
)
