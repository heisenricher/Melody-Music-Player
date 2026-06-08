package com.melody.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.melody.domain.model.Album
import com.melody.domain.model.Artist
import com.melody.domain.model.FolderNode
import com.melody.domain.model.Genre
import com.melody.domain.model.Playlist
import com.melody.domain.model.Song
import com.melody.feature.library.AlbumDetailScreen
import com.melody.feature.library.ArtistDetailScreen
import com.melody.feature.library.FolderDetailScreen
import com.melody.feature.library.GenreDetailScreen
import com.melody.feature.library.LibraryHomeScreen
import com.melody.feature.library.LyricsScreen
import com.melody.feature.library.PlayerScreen
import com.melody.feature.library.TagEditorScreen
import com.melody.feature.playlists.PlaylistDetailScreen
import com.melody.feature.playlists.PlaylistListScreen
import com.melody.feature.search.SearchScreen
import com.melody.feature.settings.AudioSettingsScreen
import com.melody.feature.settings.SettingsScreen
import com.melody.feature.settings.StorageSettingsScreen
import com.melody.feature.settings.ThemeSettingsScreen
import com.melody.player.controller.MelodyPlayer

sealed class Screen(val route: String) {
    object Library : Screen("library")
    object Playlists : Screen("playlists")
    object Search : Screen("search")
    object Settings : Screen("settings")
    object Player : Screen("player")
    object Lyrics : Screen("lyrics")
    object ThemeSettings : Screen("theme_settings")
    object AudioSettings : Screen("audio_settings")
    object StorageSettings : Screen("storage_settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MelodyNavGraph(
    navController: NavHostController,
    melodyPlayer: MelodyPlayer
) {
    var activeSongForTagEdit by remember { mutableStateOf<Song?>(null) }
    var activeAlbumForDetail by remember { mutableStateOf<Album?>(null) }
    var activeArtistForDetail by remember { mutableStateOf<Artist?>(null) }
    var activeGenreForDetail by remember { mutableStateOf<Genre?>(null) }
    var activeFolderForDetail by remember { mutableStateOf<FolderNode?>(null) }
    var activePlaylistForDetail by remember { mutableStateOf<Playlist?>(null) }

    NavHost(
        navController = navController,
        startDestination = Screen.Library.route
    ) {
        composable(Screen.Library.route) {
            LibraryHomeScreen(
                onAlbumClick = {
                    activeAlbumForDetail = it
                    navController.navigate("album_detail")
                },
                onArtistClick = {
                    activeArtistForDetail = it
                    navController.navigate("artist_detail")
                },
                onGenreClick = {
                    activeGenreForDetail = it
                    navController.navigate("genre_detail")
                },
                onFolderClick = {
                    activeFolderForDetail = it
                    navController.navigate("folder_detail")
                },
                onSongOptionsClick = {
                    activeSongForTagEdit = it
                    navController.navigate("tag_editor")
                }
            )
        }

        composable(Screen.Playlists.route) {
            PlaylistListScreen(
                onPlaylistClick = {
                    activePlaylistForDetail = it
                    navController.navigate("playlist_detail")
                }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onSongOptionsClick = {
                    activeSongForTagEdit = it
                    navController.navigate("tag_editor")
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateToTheme = { navController.navigate(Screen.ThemeSettings.route) },
                onNavigateToAudio = { navController.navigate(Screen.AudioSettings.route) },
                onNavigateToStorage = { navController.navigate(Screen.StorageSettings.route) }
            )
        }

        composable(Screen.Player.route) {
            PlayerScreen(
                onBackClick = { navController.popBackStack() },
                onLyricsClick = { navController.navigate(Screen.Lyrics.route) }
            )
        }

        composable(Screen.Lyrics.route) {
            LyricsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.ThemeSettings.route) {
            ThemeSettingsScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.AudioSettings.route) {
            AudioSettingsScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.StorageSettings.route) {
            StorageSettingsScreen(onBackClick = { navController.popBackStack() })
        }

        composable("album_detail") {
            activeAlbumForDetail?.let { album ->
                AlbumDetailScreen(
                    album = album,
                    onBackClick = { navController.popBackStack() },
                    onSongOptionsClick = {
                        activeSongForTagEdit = it
                        navController.navigate("tag_editor")
                    }
                )
            }
        }

        composable("artist_detail") {
            activeArtistForDetail?.let { artist ->
                ArtistDetailScreen(
                    artist = artist,
                    onBackClick = { navController.popBackStack() },
                    onSongOptionsClick = {
                        activeSongForTagEdit = it
                        navController.navigate("tag_editor")
                    }
                )
            }
        }

        composable("genre_detail") {
            activeGenreForDetail?.let { genre ->
                GenreDetailScreen(
                    genre = genre,
                    onBackClick = { navController.popBackStack() },
                    onSongOptionsClick = {
                        activeSongForTagEdit = it
                        navController.navigate("tag_editor")
                    }
                )
            }
        }

        composable("folder_detail") {
            activeFolderForDetail?.let { folder ->
                FolderDetailScreen(
                    folder = folder,
                    onBackClick = { navController.popBackStack() },
                    onSongOptionsClick = {
                        activeSongForTagEdit = it
                        navController.navigate("tag_editor")
                    }
                )
            }
        }

        composable("playlist_detail") {
            activePlaylistForDetail?.let { playlist ->
                PlaylistDetailScreen(
                    playlist = playlist,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        composable("tag_editor") {
            activeSongForTagEdit?.let { song ->
                TagEditorScreen(
                    song = song,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
