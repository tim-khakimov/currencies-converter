package com.timkhakimov.currenciesconverter.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.timkhakimov.currenciesconverter.data.persistence.dao.CurrenciesDao
import com.timkhakimov.currenciesconverter.data.persistence.dao.RatesDao
import com.timkhakimov.currenciesconverter.data.persistence.model.CurrenciesInfo
import com.timkhakimov.currenciesconverter.data.persistence.model.Currency
import com.timkhakimov.currenciesconverter.data.persistence.model.Rate
import com.timkhakimov.currenciesconverter.data.persistence.model.RatesInfo

@Database(
    entities = [
        Currency::class,
        CurrenciesInfo::class,
        Rate::class,
        RatesInfo::class,
    ],
    version = 1
)
abstract class ExchangeRatesDatabase : RoomDatabase() {
    abstract fun getCurrenciesDao(): CurrenciesDao
    abstract fun getRatesDao(): RatesDao
}