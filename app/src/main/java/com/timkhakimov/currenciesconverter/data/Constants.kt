package com.timkhakimov.currenciesconverter.data

object Constants {
    const val BASE_URL = "https://openexchangerates.org/api/"
    const val TIMEOUT = 20L
    const val APP_ID = "cfb2c8cba34046d3abc06d039f4d7632"
    const val BASE_CURRENCY = "USD"
    const val TIMESTAMP_DIFF: Long = 1000 * 60 * 30
    const val DATABASE_NAME = "exchange_rates_database"
    const val CURRENCIES_INFO_ID = 0
}

fun Long.isTimestampActual(): Boolean {
    return System.currentTimeMillis() - this < Constants.TIMESTAMP_DIFF
}
