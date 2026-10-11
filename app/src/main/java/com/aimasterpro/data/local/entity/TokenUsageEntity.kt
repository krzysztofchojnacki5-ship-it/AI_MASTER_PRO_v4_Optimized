package com.aimasterpro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Entity for tracking token usage and costs
 * Helps monitor spending and stay within budget
 */
@Entity(tableName = "token_usage")
data class TokenUsageEntity(
    @PrimaryKey
    val id: String,
    val model: String,
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int,
    val estimatedCost: Double, // in USD
    val timestamp: Long = System.currentTimeMillis(),
    val date: String = LocalDate.now().toString(),
    val responseTime: Long = 0, // milliseconds
    val success: Boolean = true
) {
    fun isFromToday(): Boolean {
        return date == LocalDate.now().toString()
    }
}
