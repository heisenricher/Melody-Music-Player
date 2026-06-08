package com.melody.feature.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.melody.domain.model.Genre
import com.melody.domain.model.Song

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenresScreen(
    onGenreClick: (Genre) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GenresViewModel = hiltViewModel()
) {
    val genres by viewModel.genresState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Genres") })
        },
        modifier = modifier
    ) { paddingValues ->
        if (genres.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No genres found.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                items(genres) { genre ->
                    ListItem(
                        headlineContent = { Text(genre.name) },
                        supportingContent = { Text("${genre.songCount} Songs") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onGenreClick(genre) }
                    )
                    Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreDetailScreen(
    genre: Genre,
    onBackClick: () -> Unit,
    onSongOptionsClick: (Song) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GenresViewModel = hiltViewModel()
) {
    val songs by viewModel.genreSongsState.collectAsState()

    LaunchedEffect(genre.name) {
        viewModel.selectGenre(genre.name)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(genre.name) },
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
                text = "${songs.size} Songs in ${genre.name}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (songs.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Loading...")
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
