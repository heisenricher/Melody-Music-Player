package com.melody.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToTheme: () -> Unit,
    onNavigateToAudio: () -> Unit,
    onNavigateToStorage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ListItem(
                headlineContent = { Text("Theme & Style") },
                supportingContent = { Text("App theme, dark mode, Material You dynamic colors") },
                leadingContent = { Icon(imageVector = Icons.Default.ColorLens, contentDescription = null) },
                modifier = Modifier.clickable { onNavigateToTheme() }
            )
            Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))

            ListItem(
                headlineContent = { Text("Audio Settings") },
                supportingContent = { Text("Equalizer, bass boost, virtualizer, speed controller") },
                leadingContent = { Icon(imageVector = Icons.Default.Equalizer, contentDescription = null) },
                modifier = Modifier.clickable { onNavigateToAudio() }
            )
            Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))

            ListItem(
                headlineContent = { Text("Storage, Backup & Restore") },
                supportingContent = { Text("Exclude folders, backup library DB configuration") },
                leadingContent = { Icon(imageVector = Icons.Default.FolderOpen, contentDescription = null) },
                modifier = Modifier.clickable { onNavigateToStorage() }
            )
            Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
        }
    }
}
