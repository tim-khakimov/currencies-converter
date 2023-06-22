package com.timkhakimov.currenciesconverter.data.persistence.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Currency(
    @PrimaryKey
    val code: String,
    val name: String,
)
