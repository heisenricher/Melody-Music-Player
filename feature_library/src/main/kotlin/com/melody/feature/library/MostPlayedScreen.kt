package com.melody.feature.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.melody.core.ui.components.SongRow

@Composable
fun MostPlayedScreen(
    modifier: Modifier = Modifier,
    viewModel: MostPlayedViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val playerState by viewModel.playerState.collectAsState()

    when (state) {
        is MostPlayedUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is MostPlayedUiState.Empty -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No songs played yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Play some songs to see your\nmost played tracks here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }

        is MostPlayedUiState.Success -> {
            val songs = (state as MostPlayedUiState.Success).songs
            LazyColumn(modifier = modifier.fillMaxSize()) {
                itemsIndexed(songs) { index, song ->
                    val rankColor = when (index) {
                        0 -> Color(0xFFFFD700) // Gold
                        1 -> Color(0xFFC0C0C0) // Silver
                        2 -> Color(0xFFCD7F32) // Bronze
                        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    }
                    val rankBgColor = when (index) {
                        0 -> Color(0xFFFFD700).copy(alpha = 0.15f)
                        1 -> Color(0xFFC0C0C0).copy(alpha = 0.15f)
                        2 -> Color(0xFFCD7F32).copy(alpha = 0.15f)
                        else -> Color.Transparent
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                    ) {
                        // Rank badge
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(rankBgColor)
                        ) {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (index < 3) FontWeight.Bold else FontWeight.Normal,
                                color = rankColor
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        SongRow(
                            song = song,
                            isPlaying = playerState.isPlaying && playerState.currentSong?.id == song.id,
                            onClick = { viewModel.playSong(songs, index) },
                            onLongClick = {},
                            onMoreClick = {},
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
