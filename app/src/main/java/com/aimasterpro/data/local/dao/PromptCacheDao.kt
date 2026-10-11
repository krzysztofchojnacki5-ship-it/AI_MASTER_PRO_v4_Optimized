package com.aimasterpro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aimasterpro.data.local.entity.PromptCacheEntity

@Dao
interface PromptCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PromptCacheEntity)

    @Query("SELECT * FROM prompt_cache WHERE promptHash = :promptHash LIMIT 1")
    suspend fun getByHash(promptHash: String): PromptCacheEntity?

    @Query("SELECT * FROM prompt_cache WHERE model = :model ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getRecentByModel(model: String, limit: Int = 20): List<PromptCacheEntity>

    @Query("DELETE FROM prompt_cache WHERE expiresAt < :now")
    suspend fun deleteExpired(now: Long): Int
}
