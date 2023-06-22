package com.timkhakimov.currenciesconverter.data.persistence.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RatesInfo(
    @PrimaryKey
    val base: String,
    val timestamp: Long,
)
