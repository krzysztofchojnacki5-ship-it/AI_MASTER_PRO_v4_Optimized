package com.aimasterpro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Entity for caching prompts and responses to reduce API calls
 * Helps save money and improve performance
 */
@Entity(tableName = "prompt_cache")
data class PromptCacheEntity(
    @PrimaryKey
    val promptHash: String, // SHA256 hash of the prompt
    val prompt: String,
    val response: String,
    val model: String,
    val temperature: Float = 0.7f,
    val maxTokens: Int = 2048,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000), // 7 days
    val usageCount: Int = 1 // Number of times this cached response was used
) {
    fun isExpired(): Boolean {
        return System.currentTimeMillis() > expiresAt
    }

    fun getAgeInSeconds(): Long {
        return (System.currentTimeMillis() - createdAt) / 1000
    }
}
