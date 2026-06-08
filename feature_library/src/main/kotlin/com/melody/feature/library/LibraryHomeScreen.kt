package com.melody.feature.library

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.melody.domain.model.Album
import com.melody.domain.model.Artist
import com.melody.domain.model.FolderNode
import com.melody.domain.model.Genre
import com.melody.domain.model.Song
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibraryHomeScreen(
    onAlbumClick: (Album) -> Unit,
    onArtistClick: (Artist) -> Unit,
    onGenreClick: (Genre) -> Unit,
    onFolderClick: (FolderNode) -> Unit,
    onSongOptionsClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf("Songs", "Albums", "Artists", "Genres", "Folders")
    val pagerState = rememberPagerState { tabs.size }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 16.dp,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(title) }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) { page ->
            when (page) {
                0 -> SongsScreen(onSongOptionsClick = onSongOptionsClick)
                1 -> AlbumsScreen(onAlbumClick = onAlbumClick)
                2 -> ArtistsScreen(onArtistClick = onArtistClick)
                3 -> GenresScreen(onGenreClick = onGenreClick)
                4 -> FoldersScreen(onFolderClick = onFolderClick)
            }
        }
    }
}

// Inline value to keep code simple
private val dpVal = 16
private val 16.dp get() = androidx.compose.ui.unit.dp
