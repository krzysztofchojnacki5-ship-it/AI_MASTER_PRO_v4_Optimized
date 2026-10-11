package com.aimasterpro.core

import android.util.Log

/**
 * Intelligent model selector based on capabilities, budget, and cost
 */
class ModelSelector(
    private val budgetTracker: BudgetTracker,
    private val tokenCounter: TokenCounter
) {
    private val modelCapabilities = mapOf(
        "gpt-4" to listOf("accurate", "reasoning", "code"),
        "gpt-3.5-turbo" to listOf("fast", "cheap", "general"),
        "claude-3-opus" to listOf("accurate", "long-context"),
        "claude-3-sonnet" to listOf("balanced", "reasoning"),
        "claude-3-haiku" to listOf("fast", "cheap"),
        "gemini-pro" to listOf("fast", "cheap", "multimodal")
    )

    /**
     * Select best model based on capability and budget constraints
     */
    fun selectBestModel(
        requiredCapability: String,
        estimatedTokens: Int,
        availableModels: List<String> = modelCapabilities.keys.toList(),
        dailyBudget: Double = 10.0,
        preferCheap: Boolean = true
    ): String? {
        // Filter models with required capability
        val capableModels = availableModels.filter { model ->
            modelCapabilities[model]?.contains(requiredCapability.lowercase()) == true
        }

        if (capableModels.isEmpty()) {
            Log.w("ModelSelector", "No models found with capability: $requiredCapability")
            return null
        }

        // Filter models within budget
        val affordableModels = capableModels.filter { model ->
            val estimatedCost = tokenCounter.estimateCost(estimatedTokens, model)
            budgetTracker.canAffordRequest(model, estimatedCost, dailyBudget)
        }

        if (affordableModels.isEmpty()) {
            Log.w("ModelSelector", "No models available within budget")
            return capableModels.firstOrNull() // Return cheapest capable model anyway
        }

        // Select model
        val selectedModel = if (preferCheap) {
            affordableModels.minByOrNull { model ->
                tokenCounter.estimateCost(estimatedTokens, model)
            }
        } else {
            affordableModels.maxByOrNull { model ->
                modelCapabilities[model]?.size ?: 0
            }
        }

        Log.d("ModelSelector", "Selected model: $selectedModel for capability: $requiredCapability")
        return selectedModel
    }

    /**
     * Get available capabilities
     */
    fun getAvailableCapabilities(): Set<String> {
        return modelCapabilities.values.flatten().toSet()
    }

    /**
     * Get models by capability
     */
    fun getModelsByCapability(capability: String): List<String> {
        return modelCapabilities.filter { (_, caps) ->
            caps.contains(capability.lowercase())
        }.keys.toList()
    }

    /**
     * Get all capabilities of a model
     */
    fun getModelCapabilities(model: String): List<String> {
        return modelCapabilities[model] ?: emptyList()
    }
}
