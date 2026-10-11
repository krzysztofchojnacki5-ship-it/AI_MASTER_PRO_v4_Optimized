package com.aimasterpro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Entity for saving task progress and enabling task resume
 * Allows interrupting and resuming long-running tasks
 */
@Entity(tableName = "task_checkpoints")
data class TaskCheckpointEntity(
    @PrimaryKey
    val taskId: String,
    val agentId: String,
    val taskName: String,
    val taskDescription: String,
    val status: TaskStatus = TaskStatus.IN_PROGRESS,
    val progress: Int = 0, // 0-100
    val checkpointData: String, // JSON serialized state
    val lastProcessedStep: Int = 0,
    val totalSteps: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val errorMessage: String? = null,
    val retryCount: Int = 0
) {
    fun isCompleted(): Boolean = status == TaskStatus.COMPLETED
    fun isFailed(): Boolean = status == TaskStatus.FAILED
    fun canResume(): Boolean = status == TaskStatus.IN_PROGRESS || status == TaskStatus.PAUSED
}

enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    PAUSED,
    COMPLETED,
    FAILED,
    CANCELLED
}
