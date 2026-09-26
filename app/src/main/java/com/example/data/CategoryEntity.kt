package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "point_categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val points: Int,
    val iconType: String = "timer" // "timer", "check", "flame", "star"
)
