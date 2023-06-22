package com.timkhakimov.currenciesconverter.data.repository

import com.timkhakimov.currenciesconverter.data.Constants.APP_ID
import com.timkhakimov.currenciesconverter.data.Constants.BASE_CURRENCY
import com.timkhakimov.currenciesconverter.data.isTimestampActual
import com.timkhakimov.currenciesconverter.data.persistence.dao.RatesDao
import com.timkhakimov.currenciesconverter.data.persistence.model.Rate
import com.timkhakimov.currenciesconverter.data.persistence.model.RatesInfo
import com.timkhakimov.currenciesconverter.data.persistence.model.SavedRates
import com.timkhakimov.currenciesconverter.data.rest.ExchangeRatesRestService
import com.timkhakimov.currenciesconverter.domain.RatesRepository

class RatesRepositoryImpl(
    private val exchangeRatesRestService: ExchangeRatesRestService,
    private val ratesDao: RatesDao,
) : RatesRepository {

    override suspend fun getRates(): SavedRates {
        val savedRates = ratesDao.getSavedRates(BASE_CURRENCY)
        if (savedRates != null && savedRates.timestamp.isTimestampActual()) {
            return savedRates
        }
        val latestRates = exchangeRatesRestService.getLatestRates(APP_ID, BASE_CURRENCY)
        val rates = SavedRates(
            base = latestRates.base,
            timestamp = System.currentTimeMillis(),
            rates = latestRates.rates.map { (currency, value) ->
                Rate(
                    latestRates.base,
                    currency,
                    value
                )
            }
        )
        ratesDao.saveRates(
            RatesInfo(
                rates.base,
                rates.timestamp
            ),
            rates.rates
        )
        return rates
    }
}