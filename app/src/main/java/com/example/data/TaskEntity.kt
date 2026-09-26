package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // "Trabalho", "Saúde", "Estudos", "Desenvolvimento"
    val priority: String, // "14:30 • Alta prioridade", "18:00 • Foco", etc.
    val points: Int,
    val scheduledTime: String = "",
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val linkUrl: String? = null,     // Web link / HTML link
    val imageUri: String? = null,    // Local Gallery URI or direct image URL
    val notes: String? = null
)
