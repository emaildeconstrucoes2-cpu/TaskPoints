package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class SettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val monthlyPoints: Int = 420,
    val accumulatedCoins: Int = 84,
    val coinsPer10Pts: Int = 2,
    val autoMonthlyReset: Boolean = true,
    val earlyAlert: Boolean = true,
    val isGraphZeroed: Boolean = false,
    val weeklyPointsString: String = "45,65,50,70,60,85,45",
    val currentMonthName: String = "Outubro",
    val cycleDaysRemaining: Int = 12,
    val currentCycleDay: Int = 19,
    val cycleTotalDays: Int = 31,
    val activeStreakDays: Int = 8,
    val dailyRecordPoints: Int = 95,
    val profileImageUri: String? = null
)
