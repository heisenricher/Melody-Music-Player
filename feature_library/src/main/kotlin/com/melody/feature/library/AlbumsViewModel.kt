package com.melody.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melody.domain.model.Album
import com.melody.domain.model.Song
import com.melody.domain.usecase.GetAlbumsUseCase
import com.melody.domain.usecase.GetSongsByAlbumUseCase
import com.melody.player.controller.MelodyPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    getAlbumsUseCase: GetAlbumsUseCase,
    private val getSongsByAlbumUseCase: GetSongsByAlbumUseCase,
    private val melodyPlayer: MelodyPlayer
) : ViewModel() {

    val albumsState: StateFlow<List<Album>> = getAlbumsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val selectedAlbumId = MutableStateFlow<Long?>(null)

    val albumSongsState: StateFlow<List<Song>> = selectedAlbumId
        .flatMapLatest { albumId ->
            if (albumId != null) {
                getSongsByAlbumUseCase(albumId)
            } else {
                MutableStateFlow(emptyList())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun selectAlbum(albumId: Long) {
        selectedAlbumId.value = albumId
    }

    fun playSong(songs: List<Song>, index: Int) {
        melodyPlayer.setQueue(songs, index)
    }
}
