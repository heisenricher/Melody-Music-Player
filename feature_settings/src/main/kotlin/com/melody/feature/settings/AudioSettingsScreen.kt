package com.melody.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioSettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val playbackSpeed by viewModel.playbackSpeed.collectAsState()
    val sleepTimerRemaining by viewModel.sleepTimerRemaining.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Audio Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(text = "Playback Speed: ${String.format("%.2f", playbackSpeed)}x", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = playbackSpeed,
                onValueChange = { viewModel.setPlaybackSpeed(it) },
                valueRange = 0.5f..2.0f,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Sleep Timer", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            if (sleepTimerRemaining > 0) {
                Text(
                    text = "Timer active: ${sleepTimerRemaining / 1000 / 60} minutes remaining",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.stopSleepTimer() }) {
                    Text("Stop Timer")
                }
            } else {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { viewModel.startSleepTimer(15 * 60 * 1000L) },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("15m")
                    }
                    Button(
                        onClick = { viewModel.startSleepTimer(30 * 60 * 1000L) },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("30m")
                    }
                    Button(
                        onClick = { viewModel.startSleepTimer(60 * 60 * 1000L) }
                    ) {
                        Text("60m")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Equalizer & Effects", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Bass Boost & Virtualization levels can be set directly from the player screen during active playback.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
