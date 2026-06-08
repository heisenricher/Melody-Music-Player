package com.melody.feature.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.melody.core.ui.components.SongRow
import com.melody.domain.model.Song
import com.melody.domain.repository.ScanStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsScreen(
    onSongOptionsClick: (Song) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SongsViewModel = hiltViewModel()
) {
    val uiState by viewModel.songsState.collectAsState()
    val scanStatus by viewModel.scanStatus.collectAsState()
    val playerState by viewModel.playerState.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }
    val activeSortOrder by viewModel.sortOrder.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Songs", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = { viewModel.triggerMediaScan() }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Scan Media")
                    }
                    Box {
                        TextButton(onClick = { showSortMenu = true }) {
                            Text("Sort")
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Title (A-Z)") },
                                onClick = {
                                    viewModel.setSortOrder(SortOrder.TITLE_ASC)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Title (Z-A)") },
                                onClick = {
                                    viewModel.setSortOrder(SortOrder.TITLE_DESC)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Artist") },
                                onClick = {
                                    viewModel.setSortOrder(SortOrder.ARTIST_ASC)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Date Added") },
                                onClick = {
                                    viewModel.setSortOrder(SortOrder.DATE_ADDED)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Duration") },
                                onClick = {
                                    viewModel.setSortOrder(SortOrder.DURATION)
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is SongsUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is SongsUiState.Empty -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No songs found.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        TextButton(onClick = { viewModel.triggerMediaScan() }) {
                            Text("Scan Local Storage")
                        }
                    }
                }
                is SongsUiState.Success -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(
                            items = state.songs,
                            key = { _, song -> song.id }
                        ) { index, song ->
                            val isPlaying = playerState.currentSong?.id == song.id
                            SongRow(
                                song = song,
                                isPlaying = isPlaying,
                                onClick = { viewModel.playSong(state.songs, index) },
                                onLongClick = { onSongOptionsClick(song) },
                                onMoreClick = { onSongOptionsClick(song) }
                            )
                        }
                    }
                }
            }

            // Overlay media scan progress indicator
            if (scanStatus is ScanStatus.Scanning) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                        Text(
                            text = (scanStatus as ScanStatus.Scanning).currentFile,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}
