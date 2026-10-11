package com.aimasterpro.domain.provider

import com.aimasterpro.core.BudgetTracker
import com.aimasterpro.core.ModelSelector
import com.aimasterpro.core.RateLimiter
import com.aimasterpro.core.TokenCounter

/**
 * LLM provider abstraction with local safety checks
 */
interface LlmProvider {
    suspend fun generateText(prompt: String, model: String = "gpt-3.5-turbo"): String
}

class DefaultLlmProvider(
    private val rateLimiter: RateLimiter,
    private val budgetTracker: BudgetTracker,
    private val tokenCounter: TokenCounter,
    private val modelSelector: ModelSelector
) : LlmProvider {

    override suspend fun generateText(prompt: String, model: String): String {
        val estimatedTokens = tokenCounter.countTokens(prompt)
        val estimatedCost = tokenCounter.estimateCost(estimatedTokens, model)

        // Budget check
        if (!budgetTracker.canAffordRequest(model, estimatedCost, dailyBudget = 10.0)) {
            throw IllegalStateException("Daily budget exceeded for model: $model")
        }

        // Rate limit check
        rateLimiter.waitIfNeeded()

        // Example: fallback to model selection
        val selectedModel = modelSelector.selectBestModel(
            requiredCapability = "general",
            estimatedTokens = estimatedTokens,
            availableModels = listOf("gpt-3.5-turbo", "gpt-4", "gemini-pro"),
            dailyBudget = 10.0,
            preferCheap = true
        ) ?: model

        // Simulate API response (replace with real API implementation)
        val result = "Model: $selectedModel | Estimated tokens: $estimatedTokens | Estimated cost: $${String.format("%.4f", estimatedCost)}\n\n$responseTemplate(prompt)"

        // Record spending after successful generation
        budgetTracker.recordUsage(selectedModel, estimatedCost)

        return result
    }

    private fun responseTemplate(prompt: String): String {
        return "Wysłane zapytanie: ${prompt.take(200)}${if (prompt.length > 200) "..." else ""}"
    }
}
