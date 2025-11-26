package com.localai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InferenceSessionScreen() {
    val (prompt, setPrompt) = remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Send quick prompts to the active model session.",
            style = MaterialTheme.typography.bodyMedium
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = prompt,
            onValueChange = setPrompt,
            label = { Text("Prompt") }
        )
        Button(
            onClick = { /* TODO: connect to inference runtime */ },
            modifier = Modifier.fillMaxWidth(),
            enabled = prompt.isNotBlank()
        ) {
            Text("Run inference")
        }
    }
}
