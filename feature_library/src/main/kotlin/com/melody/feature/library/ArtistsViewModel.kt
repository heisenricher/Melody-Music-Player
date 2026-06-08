package com.melody.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melody.domain.model.Artist
import com.melody.domain.model.Song
import com.melody.domain.usecase.GetArtistsUseCase
import com.melody.domain.usecase.GetSongsByArtistUseCase
import com.melody.player.controller.MelodyPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ArtistsViewModel @Inject constructor(
    getArtistsUseCase: GetArtistsUseCase,
    private val getSongsByArtistUseCase: GetSongsByArtistUseCase,
    private val melodyPlayer: MelodyPlayer
) : ViewModel() {

    val artistsState: StateFlow<List<Artist>> = getArtistsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val selectedArtistName = MutableStateFlow<String?>(null)

    val artistSongsState: StateFlow<List<Song>> = selectedArtistName
        .flatMapLatest { name ->
            if (name != null) {
                getSongsByArtistUseCase(name)
            } else {
                MutableStateFlow(emptyList())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun selectArtist(name: String) {
        selectedArtistName.value = name
    }

    fun playSong(songs: List<Song>, index: Int) {
        melodyPlayer.setQueue(songs, index)
    }
}
