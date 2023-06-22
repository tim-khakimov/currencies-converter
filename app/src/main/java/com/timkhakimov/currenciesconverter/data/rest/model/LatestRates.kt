package com.timkhakimov.currenciesconverter.data.rest.model

data class LatestRates(
    val base: String,
    val rates: Map<String, Double>
)
