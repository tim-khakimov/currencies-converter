package com.timkhakimov.currenciesconverter.data.persistence.dao

import androidx.room.*
import com.timkhakimov.currenciesconverter.data.persistence.model.Rate
import com.timkhakimov.currenciesconverter.data.persistence.model.RatesInfo
import com.timkhakimov.currenciesconverter.data.persistence.model.SavedRates

@Dao
abstract class RatesDao {

    @Query("SELECT base, timestamp from RatesInfo WHERE base = :base")
    abstract suspend fun getSavedRates(base: String): SavedRates?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertRates(rates: List<Rate>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertSavedRatesInfo(ratesInfo: RatesInfo)

    @Transaction
    open suspend fun saveRates(savedRatesRatesInfo: RatesInfo, rates: List<Rate>) {
        insertSavedRatesInfo(savedRatesRatesInfo)
        insertRates(rates)
    }
}