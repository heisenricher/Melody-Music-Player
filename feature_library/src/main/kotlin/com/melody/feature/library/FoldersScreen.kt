package com.melody.feature.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import com.melody.domain.model.FolderNode
import com.melody.domain.model.Song

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoldersScreen(
    onFolderClick: (FolderNode) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FoldersViewModel = hiltViewModel()
) {
    val folders by viewModel.foldersState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Folders") })
        },
        modifier = modifier
    ) { paddingValues ->
        if (folders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No folders found.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                items(folders) { folder ->
                    ListItem(
                        headlineContent = { Text(folder.name) },
                        supportingContent = { Text("${folder.songsCount} Songs — ${folder.path}") },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = if (folder.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingContent = {
                            Row {
                                IconButton(onClick = { viewModel.pinFolder(folder.path, !folder.isPinned) }) {
                                    Icon(
                                        imageVector = Icons.Default.PushPin,
                                        contentDescription = "Pin Folder",
                                        tint = if (folder.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                    )
                                }
                                IconButton(onClick = { viewModel.excludeFolder(folder.path, !folder.isExcluded) }) {
                                    Icon(
                                        imageVector = Icons.Default.Block,
                                        contentDescription = "Exclude Folder",
                                        tint = if (folder.isExcluded) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onFolderClick(folder) }
                    )
                    Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderDetailScreen(
    folder: FolderNode,
    onBackClick: () -> Unit,
    onSongOptionsClick: (Song) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FoldersViewModel = hiltViewModel()
) {
    val songs by viewModel.folderSongsState.collectAsState()

    LaunchedEffect(folder.path) {
        viewModel.selectFolder(folder.path)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(folder.name) },
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
        ) {
            Text(
                text = "${songs.size} Songs in ${folder.name}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (songs.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No songs found or excluded folder.")
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(songs) { index, song ->
                        SongRow(
                            song = song,
                            isPlaying = false,
                            onClick = { viewModel.playSong(songs, index) },
                            onLongClick = { onSongOptionsClick(song) },
                            onMoreClick = { onSongOptionsClick(song) }
                        )
                    }
                }
            }
        }
    }
}
