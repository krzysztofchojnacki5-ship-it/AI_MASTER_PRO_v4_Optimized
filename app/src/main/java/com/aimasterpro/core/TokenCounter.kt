package com.aimasterpro.core

/**
 * Token counter for estimating API costs
 * Implements approximation: ~1 token = 4 characters
 * More accurate counting requires model-specific tokenizers
 */
class TokenCounter {

    /**
     * Count tokens in text (approximation)
     */
    fun countTokens(text: String): Int {
        if (text.isEmpty()) return 0
        
        // Basic approximation: 1 token ≈ 4 characters
        val estimatedTokens = (text.length / 4.0).toInt()
        
        // Ensure at least 1 token
        return maxOf(1, estimatedTokens)
    }

    /**
     * Count tokens with more detailed breakdown
     */
    fun countTokensDetailed(text: String): TokenCountBreakdown {
        val tokens = countTokens(text)
        val words = text.split("\\s+".toRegex()).size
        val characters = text.length
        val lines = text.split("\n").size

        return TokenCountBreakdown(
            totalTokens = tokens,
            words = words,
            characters = characters,
            lines = lines
        )
    }

    /**
     * Estimate cost based on model pricing
     * Prices in USD per 1M tokens
     */
    fun estimateCost(
        tokens: Int,
        model: String,
        isCompletion: Boolean = false
    ): Double {
        val pricePerMToken = getPricePerMillionTokens(model, isCompletion)
        return (tokens * pricePerMToken) / 1_000_000
    }

    /**
     * Get pricing for different models
     * Update these based on current API pricing
     */
    private fun getPricePerMillionTokens(model: String, isCompletion: Boolean): Double {
        return when {
            model.contains("gpt-4", ignoreCase = true) && !isCompletion -> 0.03
            model.contains("gpt-4", ignoreCase = true) && isCompletion -> 0.06
            model.contains("gpt-3.5", ignoreCase = true) && !isCompletion -> 0.0005
            model.contains("gpt-3.5", ignoreCase = true) && isCompletion -> 0.0015
            model.contains("claude-3", ignoreCase = true) -> 0.003
            model.contains("claude-2", ignoreCase = true) -> 0.008
            model.contains("gemini", ignoreCase = true) -> 0.0001
            else -> 0.001 // Default fallback
        }
    }

    data class TokenCountBreakdown(
        val totalTokens: Int,
        val words: Int,
        val characters: Int,
        val lines: Int
    )
}
