package com.melody.feature.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melody.domain.model.Song
import com.melody.domain.usecase.GetSongsInPlaylistUseCase
import com.melody.domain.usecase.AddSongsToPlaylistUseCase
import com.melody.domain.usecase.RemoveSongsFromPlaylistUseCase
import com.melody.domain.usecase.ExportM3UUseCase
import com.melody.player.controller.MelodyPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val getSongsInPlaylistUseCase: GetSongsInPlaylistUseCase,
    private val addSongsToPlaylistUseCase: AddSongsToPlaylistUseCase,
    private val removeSongsFromPlaylistUseCase: RemoveSongsFromPlaylistUseCase,
    private val exportM3UUseCase: ExportM3UUseCase,
    private val melodyPlayer: MelodyPlayer
) : ViewModel() {

    private val activePlaylistId = MutableStateFlow<Long?>(null)

    val playlistSongsState: StateFlow<List<Song>> = activePlaylistId
        .flatMapLatest { id ->
            if (id != null) {
                getSongsInPlaylistUseCase(id)
            } else {
                MutableStateFlow(emptyList())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun loadPlaylist(id: Long) {
        activePlaylistId.value = id
    }

    fun playSong(songs: List<Song>, index: Int) {
        melodyPlayer.setQueue(songs, index)
    }

    fun removeSongs(songIds: List<Long>) {
        val playlistId = activePlaylistId.value ?: return
        viewModelScope.launch {
            removeSongsFromPlaylistUseCase(playlistId, songIds)
        }
    }

    fun addSongs(songIds: List<Long>) {
        val playlistId = activePlaylistId.value ?: return
        viewModelScope.launch {
            addSongsToPlaylistUseCase(playlistId, songIds)
        }
    }

    fun exportToM3U(destinationPath: String) {
        val playlistId = activePlaylistId.value ?: return
        viewModelScope.launch {
            exportM3UUseCase(playlistId, destinationPath)
        }
    }
}
