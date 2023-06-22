package com.timkhakimov.currenciesconverter.domain

import com.timkhakimov.currenciesconverter.data.persistence.model.SavedRates

interface RatesRepository {

    suspend fun getRates(): SavedRates
}