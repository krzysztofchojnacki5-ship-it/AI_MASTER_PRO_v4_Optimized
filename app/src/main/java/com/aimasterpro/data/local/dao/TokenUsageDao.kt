package com.aimasterpro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aimasterpro.data.local.entity.TokenUsageEntity

@Dao
interface TokenUsageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TokenUsageEntity)

    @Query("SELECT * FROM token_usage WHERE model = :model AND date = :date ORDER BY timestamp DESC")
    suspend fun getUsageForToday(model: String, date: String): List<TokenUsageEntity>

    @Query("SELECT SUM(totalTokens) FROM token_usage WHERE model = :model AND date = :date")
    suspend fun getTotalTokensForDate(model: String, date: String): Int?

    @Query("SELECT SUM(estimatedCost) FROM token_usage WHERE model = :model AND date = :date")
    suspend fun getTotalCostForDate(model: String, date: String): Double?
}
