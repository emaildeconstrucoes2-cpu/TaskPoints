package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<SettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsSync(): SettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: SettingsEntity)

    @Query("UPDATE app_settings SET accumulatedCoins = accumulatedCoins + :coinsEarned, monthlyPoints = :newPoints WHERE id = 1")
    suspend fun convertPoints(coinsEarned: Int, newPoints: Int)

    @Query("UPDATE app_settings SET monthlyPoints = :points WHERE id = 1")
    suspend fun updateMonthlyPoints(points: Int)

    @Query("UPDATE app_settings SET coinsPer10Pts = :rate WHERE id = 1")
    suspend fun updateRate(rate: Int)

    @Query("UPDATE app_settings SET autoMonthlyReset = :reset, earlyAlert = :alert WHERE id = 1")
    suspend fun updateToggles(reset: Boolean, alert: Boolean)

    @Query("UPDATE app_settings SET accumulatedCoins = 0 WHERE id = 1")
    suspend fun resetCoins()

    @Query("UPDATE app_settings SET monthlyPoints = 0, accumulatedCoins = 0, isGraphZeroed = 1, weeklyPointsString = '0,0,0,0,0,0,0' WHERE id = 1")
    suspend fun resetAll()

    @Query("UPDATE app_settings SET isGraphZeroed = 1, weeklyPointsString = '0,0,0,0,0,0,0' WHERE id = 1")
    suspend fun resetGraph()

    @Query("UPDATE app_settings SET monthlyPoints = 0, accumulatedCoins = 0 WHERE id = 1")
    suspend fun resetPointsAndCoins()
}
