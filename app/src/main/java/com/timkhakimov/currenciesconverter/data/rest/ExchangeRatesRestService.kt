package com.timkhakimov.currenciesconverter.data.rest

import com.timkhakimov.currenciesconverter.data.rest.model.LatestRates
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRatesRestService {

    @GET("currencies.json")
    suspend fun getCurrencies(): Map<String, String>

    @GET("latest.json")
    suspend fun getLatestRates(
        @Query("app_id") appId: String,
        @Query("base") base: String,
    ): LatestRates
}
