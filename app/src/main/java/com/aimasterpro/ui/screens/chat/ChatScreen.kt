package com.aimasterpro.ui.screens.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aimasterpro.core.BudgetTracker
import com.aimasterpro.core.RateLimiter
import com.aimasterpro.core.TokenCounter

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = remember { ChatViewModel() }
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(title = { Text("AI Master Pro Chat") })

        // Budget and usage indicators
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(
                onClick = {},
                label = { Text("Tokens: ${state.estimatedTokens}") }
            )
            AssistChip(
                onClick = {},
                label = { Text("Budget: ${String.format("%.2f", state.estimatedCost)}$") }
            )
            AssistChip(
                onClick = {},
                label = { Text("Status: ${state.status}") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.messages) { message ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Smart input area
        OutlinedTextField(
            value = state.prompt,
            onValueChange = { viewModel.onPromptChanged(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Wpisz prompt...") },
            minLines = 3,
            maxLines = 6
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Character count / warning
            Text(
                text = "${state.prompt.length}/${state.maxCharacters}",
                color = if (state.prompt.length > state.maxCharacters) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )

            Button(
                onClick = { viewModel.sendPrompt() },
                enabled = state.prompt.isNotBlank() && !state.isSending
            ) {
                Text(if (state.isSending) "Wysyłanie..." else "Wyślij")
            }
        }

        if (state.prompt.length > state.maxCharacters) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { viewModel.copyToNotepadSuggestion() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tekst przekracza limit. Skopiuj do notatnika")
            }
        }
    }
}
