package com.melody.feature.playlists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlaylistRemove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.melody.core.ui.components.SongRow
import com.melody.domain.model.Playlist
import com.melody.domain.model.Song

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    playlist: Playlist,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlaylistDetailViewModel = hiltViewModel()
) {
    val songs by viewModel.playlistSongsState.collectAsState()

    LaunchedEffect(playlist.id) {
        viewModel.loadPlaylist(playlist.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(playlist.name) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Export to standard Downloads folder
                        val path = "/storage/emulated/0/Download/Melody_${playlist.name}.m3u"
                        viewModel.exportToM3U(path)
                    }) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = "Export M3U")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        if (songs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Playlist is empty.\nLong press any song to add it here.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(songs, key = { _, song -> song.id }) { index, song ->
                        SongRow(
                            song = song,
                            isPlaying = false, // handled by player state generally
                            onClick = { viewModel.playSong(songs, index) },
                            onLongClick = {},
                            onMoreClick = {
                                viewModel.removeSongs(listOf(song.id))
                            }
                        )
                    }
                }
            }
        }
    }
}
