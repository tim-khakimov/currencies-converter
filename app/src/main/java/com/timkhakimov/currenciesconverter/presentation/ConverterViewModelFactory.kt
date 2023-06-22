package com.timkhakimov.currenciesconverter.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.timkhakimov.currenciesconverter.data.persistence.ExchangeRatesDatabase
import com.timkhakimov.currenciesconverter.data.repository.CurrenciesRepositoryImpl
import com.timkhakimov.currenciesconverter.data.repository.RatesRepositoryImpl
import com.timkhakimov.currenciesconverter.data.rest.Api
import com.timkhakimov.currenciesconverter.data.rest.ExchangeRatesRestService
import com.timkhakimov.currenciesconverter.domain.ConverterInteractor
import com.timkhakimov.currenciesconverter.domain.CurrenciesInteractor

class ConverterViewModelFactory(
    private val database: ExchangeRatesDatabase,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConverterViewModel::class.java)) {
            val restService = Api.retrofit.create(ExchangeRatesRestService::class.java)
            return ConverterViewModel(
                currenciesInteractor = CurrenciesInteractor(
                    currenciesRepository = CurrenciesRepositoryImpl(
                        restService,
                        database.getCurrenciesDao()
                    )
                ),
                converterInteractor = ConverterInteractor(
                    ratesRepository = RatesRepositoryImpl(
                        restService,
                        database.getRatesDao()
                    )
                ),
                reducer = ConverterViewModelReducer(),
                mapper = UiStateMapper(),
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}