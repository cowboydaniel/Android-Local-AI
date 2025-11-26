package com.localai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    val (useGpu, setUseGpu) = remember { mutableStateOf(true) }
    val (telemetryEnabled, setTelemetryEnabled) = remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Configure runtime behavior before sessions are initialized.",
            style = MaterialTheme.typography.bodyMedium
        )
        SettingsRow(
            title = "Use GPU acceleration",
            description = "Prefer GPU when available for inference workloads.",
            checked = useGpu,
            onToggle = setUseGpu
        )
        SettingsRow(
            title = "Share anonymous telemetry",
            description = "Help improve the app by sharing crash and usage statistics.",
            checked = telemetryEnabled,
            onToggle = setTelemetryEnabled
        )
    }
}

@Composable
private fun SettingsRow(
    title: String,
    description: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(text = description, style = MaterialTheme.typography.bodySmall)
        Switch(
            checked = checked,
            onCheckedChange = onToggle
        )
    }
}
