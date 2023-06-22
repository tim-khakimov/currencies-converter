package com.timkhakimov.currenciesconverter.domain

import com.timkhakimov.currenciesconverter.data.persistence.model.Currency

class CurrenciesInteractor(
    private val currenciesRepository: CurrenciesRepository
) {

    suspend fun getCurrencies(): List<Currency> {
        return currenciesRepository.getCurrencies()
    }
}