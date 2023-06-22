package com.timkhakimov.currenciesconverter

import android.app.Application
import androidx.room.Room
import com.timkhakimov.currenciesconverter.data.Constants.DATABASE_NAME
import com.timkhakimov.currenciesconverter.data.persistence.ExchangeRatesDatabase

class App : Application() {

    val database: ExchangeRatesDatabase by lazy {
        Room.databaseBuilder(
            this,
            ExchangeRatesDatabase::class.java,
            DATABASE_NAME
        ).build()
    }
}