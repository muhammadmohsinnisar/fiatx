package com.mohsin.fiatx.data.remote

import com.mohsin.fiatx.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log

class RetrofitClient {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val primaryService: CurrencyApiService by lazy {
        createRetrofit(BuildConfig.CURRENCY_API_BASE_URL).create(CurrencyApiService::class.java)
    }

    private val fallbackService: CurrencyApiService by lazy {
        createRetrofit(BuildConfig.CURRENCY_API_FALLBACK_URL).create(CurrencyApiService::class.java)
    }

    private suspend fun <T> safeCallWithFallback(
        callPrimary: suspend CurrencyApiService.() -> T,
        callFallback: suspend CurrencyApiService.() -> T
    ): T {
        return try {
            primaryService.callPrimary()
        } catch (e: Exception) {
            Log.w("RetrofitClient", "Primary API failed. Falling back.", e)
            fallbackService.callFallback()
        }
    }

    suspend fun getSupportedCurrencies(): Map<String, String> = safeCallWithFallback(
        callPrimary = { primaryService.getSupportedCurrencies() },
        callFallback = { fallbackService.getSupportedCurrencies() }
    )

    suspend fun getLatestRatesForBase(base: String): Map<String, Any> = safeCallWithFallback(
        callPrimary = { primaryService.getLatestRatesForBase(base) },
        callFallback = { fallbackService.getLatestRatesForBase(base) }
    )
}
