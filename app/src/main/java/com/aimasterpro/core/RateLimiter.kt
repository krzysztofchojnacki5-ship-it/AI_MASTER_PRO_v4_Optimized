package com.aimasterpro.core

import android.util.Log
import kotlinx.coroutines.delay
import java.time.LocalDateTime

/**
 * Rate limiter to prevent exceeding API limits
 * Supports per-minute, per-hour, and per-day limits
 */
class RateLimiter(
    val requestsPerMinute: Int = 3,
    val requestsPerHour: Int = 20,
    val requestsPerDay: Int = 100
) {
    private val requestTimestamps = mutableListOf<Long>()
    private val minDelayMs = 1000L / requestsPerMinute // Minimum delay between requests
    private var lastRequestTime = 0L

    /**
     * Check if request can be made without exceeding limits
     */
    fun canMakeRequest(): Boolean {
        val now = System.currentTimeMillis()
        cleanOldRequests(now)

        // Check day limit
        val dayLimitOk = requestTimestamps.count { isWithinLastDay(it, now) } < requestsPerDay
        
        // Check hour limit
        val hourLimitOk = requestTimestamps.count { isWithinLastHour(it, now) } < requestsPerHour
        
        // Check minute limit
        val minuteLimitOk = requestTimestamps.count { isWithinLastMinute(it, now) } < requestsPerMinute

        return dayLimitOk && hourLimitOk && minuteLimitOk
    }

    /**
     * Wait until request can be made
     */
    suspend fun waitIfNeeded() {
        val now = System.currentTimeMillis()
        val timeSinceLastRequest = now - lastRequestTime

        if (timeSinceLastRequest < minDelayMs) {
            val delayTime = minDelayMs - timeSinceLastRequest
            Log.d("RateLimiter", "Waiting ${delayTime}ms before next request")
            delay(delayTime)
        }

        while (!canMakeRequest()) {
            Log.w("RateLimiter", "Rate limit exceeded, waiting 5 seconds...")
            delay(5000)
        }

        recordRequest()
    }

    /**
     * Record a request
     */
    fun recordRequest() {
        lastRequestTime = System.currentTimeMillis()
        requestTimestamps.add(lastRequestTime)
    }

    /**
     * Get remaining requests for current period
     */
    fun getRemainingRequests(): Map<String, Int> {
        val now = System.currentTimeMillis()
        cleanOldRequests(now)

        return mapOf(
            "minute" to (requestsPerMinute - requestTimestamps.count { isWithinLastMinute(it, now) }),
            "hour" to (requestsPerHour - requestTimestamps.count { isWithinLastHour(it, now) }),
            "day" to (requestsPerDay - requestTimestamps.count { isWithinLastDay(it, now) })
        )
    }

    private fun cleanOldRequests(now: Long) {
        requestTimestamps.removeAll { now - it > 24 * 60 * 60 * 1000 } // Remove older than 24h
    }

    private fun isWithinLastMinute(timestamp: Long, now: Long): Boolean {
        return now - timestamp < 60 * 1000
    }

    private fun isWithinLastHour(timestamp: Long, now: Long): Boolean {
        return now - timestamp < 60 * 60 * 1000
    }

    private fun isWithinLastDay(timestamp: Long, now: Long): Boolean {
        return now - timestamp < 24 * 60 * 60 * 1000
    }
}
