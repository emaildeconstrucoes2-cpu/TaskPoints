package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversionDao {
    @Query("SELECT * FROM conversions ORDER BY timestamp DESC")
    fun getAllConversions(): Flow<List<ConversionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversion(conversion: ConversionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(conversions: List<ConversionEntity>)

    @Query("SELECT COUNT(*) FROM conversions")
    suspend fun getCount(): Int

    @Query("DELETE FROM conversions")
    suspend fun clearAll()
}
