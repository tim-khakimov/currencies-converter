package com.timkhakimov.currenciesconverter.domain

import com.timkhakimov.currenciesconverter.data.persistence.model.Currency

interface CurrenciesRepository {

    suspend fun getCurrencies(): List<Currency>
}
