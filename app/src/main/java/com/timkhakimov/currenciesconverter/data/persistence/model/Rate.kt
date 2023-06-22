package com.timkhakimov.currenciesconverter.data.persistence.model

import androidx.room.Entity

@Entity(primaryKeys = ["baseCurrencyCode", "currencyCode"])
data class Rate(
    val baseCurrencyCode: String,
    val currencyCode: String,
    val value: Double,
)
