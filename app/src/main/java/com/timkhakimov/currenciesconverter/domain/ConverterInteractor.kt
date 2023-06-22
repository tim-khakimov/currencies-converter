package com.timkhakimov.currenciesconverter.domain

class ConverterInteractor(
    private val ratesRepository: RatesRepository
) {

    suspend fun getCurrenciesRates(
        targetCurrency: String,
        value: Double,
    ): Map<String, Double?> {
        val latestRates = ratesRepository.getRates()
        val currencyRate = latestRates.rates.find {
            it.currencyCode == targetCurrency
        }?.value
        val valueInBaseCurrency: Double? = currencyRate ?.let {
            value / it
        }
        val currencyValuesMap = hashMapOf<String, Double?>()
        latestRates.rates.forEach { rate ->
            currencyValuesMap[rate.currencyCode] = valueInBaseCurrency?.let {
                it * rate.value
            }
        }
        return currencyValuesMap
    }
}
