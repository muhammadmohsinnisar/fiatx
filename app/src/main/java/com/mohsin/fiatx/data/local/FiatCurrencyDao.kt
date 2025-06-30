package com.mohsin.fiatx.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FiatCurrencyDao {

    @Query("SELECT COUNT(*) FROM fiat_currencies")
    suspend fun getCount(): Int

    @Query("SELECT * FROM fiat_currencies")
    suspend fun getAll(): List<FiatCurrencyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(currencies: List<FiatCurrencyEntity>)

    @Query("SELECT MAX(lastUpdated) FROM fiat_currencies")
    suspend fun getLastUpdatedTime(): Long?
}
