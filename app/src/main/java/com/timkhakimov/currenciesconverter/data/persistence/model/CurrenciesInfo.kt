package com.timkhakimov.currenciesconverter.data.persistence.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrenciesInfo(
    @PrimaryKey
    val id: Int,
    val timestamp: Long,
)
