package com.aimasterpro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aimasterpro.data.local.entity.TaskCheckpointEntity

@Dao
interface TaskCheckpointDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TaskCheckpointEntity)

    @Query("SELECT * FROM task_checkpoints WHERE taskId = :taskId LIMIT 1")
    suspend fun getByTaskId(taskId: String): TaskCheckpointEntity?

    @Query("SELECT * FROM task_checkpoints WHERE status IN ('IN_PROGRESS', 'PAUSED') ORDER BY updatedAt DESC")
    suspend fun getActiveTasks(): List<TaskCheckpointEntity>

    @Query("UPDATE task_checkpoints SET status = :status, progress = :progress, updatedAt = :updatedAt WHERE taskId = :taskId")
    suspend fun updateStatus(taskId: String, status: String, progress: Int, updatedAt: Long): Int
}
