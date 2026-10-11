package com.aimasterpro.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aimasterpro.data.local.dao.PromptCacheDao
import com.aimasterpro.data.local.dao.TokenUsageDao
import com.aimasterpro.data.local.dao.TaskCheckpointDao
import com.aimasterpro.data.local.entity.PromptCacheEntity
import com.aimasterpro.data.local.entity.TokenUsageEntity
import com.aimasterpro.data.local.entity.TaskCheckpointEntity

@Database(
    entities = [
        PromptCacheEntity::class,
        TokenUsageEntity::class,
        TaskCheckpointEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun promptCacheDao(): PromptCacheDao
    abstract fun tokenUsageDao(): TokenUsageDao
    abstract fun taskCheckpointDao(): TaskCheckpointDao
}
