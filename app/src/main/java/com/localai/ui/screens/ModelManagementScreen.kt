package com.localai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ModelManagementScreen() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Manage your installed and remote models.",
            style = MaterialTheme.typography.bodyMedium
        )
        Button(onClick = { /* TODO: wire model discovery */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Discover models")
        }
        Button(onClick = { /* TODO: import a local model */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Import from device")
        }
    }
}
