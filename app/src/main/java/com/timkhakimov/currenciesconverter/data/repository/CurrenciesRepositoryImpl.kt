package com.timkhakimov.currenciesconverter.data.repository

import com.timkhakimov.currenciesconverter.data.Constants.CURRENCIES_INFO_ID
import com.timkhakimov.currenciesconverter.data.isTimestampActual
import com.timkhakimov.currenciesconverter.data.persistence.dao.CurrenciesDao
import com.timkhakimov.currenciesconverter.data.persistence.model.CurrenciesInfo
import com.timkhakimov.currenciesconverter.data.persistence.model.Currency
import com.timkhakimov.currenciesconverter.data.rest.ExchangeRatesRestService
import com.timkhakimov.currenciesconverter.domain.CurrenciesRepository

class CurrenciesRepositoryImpl(
    private val exchangeRatesRestService: ExchangeRatesRestService,
    private val currenciesDao: CurrenciesDao,
) : CurrenciesRepository {

    override suspend fun getCurrencies(): List<Currency> {
        val currenciesInfo = currenciesDao.getCurrenciesInfo(CURRENCIES_INFO_ID)
        if (currenciesInfo != null && currenciesInfo.timestamp.isTimestampActual()) {
            return currenciesDao.getCurrencies()
        }
        val remoteCurrencies = exchangeRatesRestService.getCurrencies().map {
            Currency(it.key, it.value)
        }
        currenciesDao.saveCurrencies(
            currenciesInfo = CurrenciesInfo(
                CURRENCIES_INFO_ID,
                System.currentTimeMillis()
            ),
            currencies = remoteCurrencies
        )
        return remoteCurrencies
    }
}
