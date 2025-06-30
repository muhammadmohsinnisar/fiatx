package com.mohsin.fiatx.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exchange_rates")
data class ExchangeRateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val base: String,
    val target: String,
    val rate: Double,
    val date: String,
    val timestamp: Long
)
