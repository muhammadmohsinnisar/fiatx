package com.mohsin.fiatx.data.local

import androidx.room.*

@Dao
interface ExchangeRateDao {

    @Query("SELECT * FROM exchange_rates WHERE base = :base")
    suspend fun getRatesByBase(base: String): List<ExchangeRateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(rates: List<ExchangeRateEntity>)

    @Query("DELETE FROM exchange_rates WHERE base = :base")
    suspend fun deleteRatesForBase(base: String)

    @Query("SELECT MAX(timestamp) FROM exchange_rates WHERE base = :base")
    suspend fun getLastUpdatedTime(base: String): Long?
}
