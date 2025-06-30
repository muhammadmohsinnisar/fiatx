package com.mohsin.fiatx.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fiat_currencies")
data class FiatCurrencyEntity(
    @PrimaryKey val code: String,
    val name: String,
    val lastUpdated: Long = System.currentTimeMillis()
)
