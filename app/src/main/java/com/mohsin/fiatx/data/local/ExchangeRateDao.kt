package com.mohsin.fiatx.data.local

import androidx.room.*

@Dao
interface ExchangeRateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(rates: List<ExchangeRateEntity>)

    @Query("DELETE FROM exchange_rates WHERE base = :base")
    suspend fun deleteRatesForBase(base: String)

    @Query("SELECT MAX(timestamp) FROM exchange_rates WHERE base = :base")
    suspend fun getLastUpdatedTime(base: String): Long?

    @Query("SELECT rate FROM exchange_rates WHERE base = :base AND target = :target LIMIT 1")
    suspend fun getRate(base: String, target: String): Double?
}
