package com.aimasterpro.core

import android.util.Log
import java.time.LocalDate
import kotlin.math.roundToInt

/**
 * Budget tracker for monitoring API spending per model and time period
 */
class BudgetTracker {
    private val dailyUsage = mutableMapOf<String, MutableMap<String, Double>>() // model -> date -> usage
    private val monthlyUsage = mutableMapOf<String, Double>() // model -> total usage

    /**
     * Check if request is within budget
     */
    fun canAffordRequest(
        model: String,
        estimatedCost: Double,
        dailyBudget: Double = 10.0, // $10 per model per day
        monthlyBudget: Double = 200.0 // $200 per model per month
    ): Boolean {
        val todayUsage = getDailyUsage(model)
        val monthlyUsageAmount = getMonthlyUsage(model)

        val canAffordDaily = (todayUsage + estimatedCost) <= dailyBudget
        val canAffordMonthly = (monthlyUsageAmount + estimatedCost) <= monthlyBudget

        Log.d(
            "BudgetTracker",
            "Model: $model | Today: \$${todayUsage.roundToInt()}/\$${dailyBudget.roundToInt()} | Monthly: \$${monthlyUsageAmount.roundToInt()}/\$${monthlyBudget.roundToInt()}"
        )

        return canAffordDaily && canAffordMonthly
    }

    /**
     * Record API usage
     */
    fun recordUsage(model: String, cost: Double) {
        val today = LocalDate.now().toString()
        
        // Daily tracking
        dailyUsage.getOrPut(model) { mutableMapOf() }
            .merge(today, cost) { a, b -> a + b }

        // Monthly tracking
        monthlyUsage.merge(model, cost) { a, b -> a + b }

        Log.d("BudgetTracker", "Recorded \$${String.format("%.4f", cost)} for $model")
    }

    /**
     * Get daily usage for model
     */
    fun getDailyUsage(model: String): Double {
        val today = LocalDate.now().toString()
        return dailyUsage[model]?.get(today) ?: 0.0
    }

    /**
     * Get monthly usage for model
     */
    fun getMonthlyUsage(model: String): Double {
        return monthlyUsage[model] ?: 0.0
    }

    /**
     * Get usage summary for all models
     */
    fun getUsageSummary(): Map<String, ModelUsageSummary> {
        val today = LocalDate.now().toString()
        return dailyUsage.mapValues { (model, dateMap) ->
            ModelUsageSummary(
                model = model,
                dailyUsage = dateMap[today] ?: 0.0,
                monthlyUsage = monthlyUsage[model] ?: 0.0,
                requestsToday = dateMap.size
            )
        }
    }

    /**
     * Reset daily budget for new day
     */
    fun resetDailyBudget(model: String) {
        val yesterday = LocalDate.now().minusDays(1).toString()
        dailyUsage[model]?.remove(yesterday)
        Log.d("BudgetTracker", "Reset daily budget for $model")
    }

    data class ModelUsageSummary(
        val model: String,
        val dailyUsage: Double,
        val monthlyUsage: Double,
        val requestsToday: Int
    )
}
