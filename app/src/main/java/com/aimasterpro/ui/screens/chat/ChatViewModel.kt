package com.aimasterpro.ui.screens.chat

import androidx.compose.runtime.*
import com.aimasterpro.core.TokenCounter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ChatUiState(
    val prompt: String = "",
    val messages: List<String> = emptyList(),
    val estimatedTokens: Int = 0,
    val estimatedCost: Double = 0.0,
    val status: String = "Ready",
    val isSending: Boolean = false,
    val maxCharacters: Int = 1800
)

class ChatViewModel : androidx.lifecycle.ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val tokenCounter = TokenCounter()

    fun onPromptChanged(newPrompt: String) {
        val tokens = tokenCounter.countTokens(newPrompt)
        val cost = tokenCounter.estimateCost(tokens, "gpt-3.5-turbo")

        _uiState.value = _uiState.value.copy(
            prompt = newPrompt,
            estimatedTokens = tokens,
            estimatedCost = cost
        )
    }

    fun sendPrompt() {
        val prompt = _uiState.value.prompt.trim()
        if (prompt.isEmpty()) return

        _uiState.value = _uiState.value.copy(
            isSending = true,
            status = "Sending",
            messages = _uiState.value.messages + "Ty: $prompt"
        )

        // Example: simulate response (replace with LLM provider call)
        val response = "Odpowiedź modelu dla promptu o długości ${prompt.length} znaków."

        _uiState.value = _uiState.value.copy(
            isSending = false,
            status = "Completed",
            messages = _uiState.value.messages + "Master: $response",
            prompt = ""
        )
    }

    fun copyToNotepadSuggestion() {
        _uiState.value = _uiState.value.copy(
            status = "Copied suggestion",
            prompt = _uiState.value.prompt
        )
    }
}
