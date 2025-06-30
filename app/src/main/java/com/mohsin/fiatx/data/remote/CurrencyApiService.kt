package com.mohsin.fiatx.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApiService {

    @GET("currencies.json")
    suspend fun getSupportedCurrencies(): Map<String, String>

    @GET("currencies/{base}.json")
    suspend fun getLatestRatesForBase(@Path("base") base: String): Map<String, Any>
}
