package com.timkhakimov.currenciesconverter.data.persistence.dao

import androidx.room.*
import com.timkhakimov.currenciesconverter.data.persistence.model.CurrenciesInfo
import com.timkhakimov.currenciesconverter.data.persistence.model.Currency

@Dao
abstract class CurrenciesDao {

    @Query("SELECT * from Currency")
    abstract suspend fun getCurrencies(): List<Currency>

    @Query("SELECT * from CurrenciesInfo WHERE id = :id")
    abstract suspend fun getCurrenciesInfo(id: Int): CurrenciesInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCurrencies(currencies: List<Currency>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCurrenciesInfo(currenciesInfo: CurrenciesInfo)

    @Transaction
    open suspend fun saveCurrencies(currenciesInfo: CurrenciesInfo, currencies: List<Currency>) {
        insertCurrenciesInfo(currenciesInfo)
        insertCurrencies(currencies)
    }
}