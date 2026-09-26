package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversions")
data class ConversionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val monthCode: String,     // e.g. "OUT", "SET", "AGO", "JUL"
    val monthName: String,     // e.g. "Outubro", "Setembro"
    val pointsConverted: Int,
    val coinsEarned: Int,
    val timestamp: Long = System.currentTimeMillis()
)
