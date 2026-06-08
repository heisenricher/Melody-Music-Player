package com.melody.feature.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melody.domain.model.Playlist
import com.melody.domain.usecase.GetPlaylistsUseCase
import com.melody.domain.usecase.CreatePlaylistUseCase
import com.melody.domain.usecase.DeletePlaylistUseCase
import com.melody.domain.usecase.ImportM3UUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    getPlaylistsUseCase: GetPlaylistsUseCase,
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase,
    private val importM3UUseCase: ImportM3UUseCase
) : ViewModel() {

    val playlistsState: StateFlow<List<Playlist>> = getPlaylistsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            createPlaylistUseCase(name)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            deletePlaylistUseCase(playlistId)
        }
    }

    fun importM3U(filePath: String) {
        viewModelScope.launch {
            importM3UUseCase(filePath)
        }
    }
}
