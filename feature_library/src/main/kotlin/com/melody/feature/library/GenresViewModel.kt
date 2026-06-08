package com.melody.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melody.domain.model.Genre
import com.melody.domain.model.Song
import com.melody.domain.usecase.GetGenresUseCase
import com.melody.domain.usecase.GetSongsByGenreUseCase
import com.melody.player.controller.MelodyPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GenresViewModel @Inject constructor(
    getGenresUseCase: GetGenresUseCase,
    private val getSongsByGenreUseCase: GetSongsByGenreUseCase,
    private val melodyPlayer: MelodyPlayer
) : ViewModel() {

    val genresState: StateFlow<List<Genre>> = getGenresUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val selectedGenreName = MutableStateFlow<String?>(null)

    val genreSongsState: StateFlow<List<Song>> = selectedGenreName
        .flatMapLatest { name ->
            if (name != null) {
                getSongsByGenreUseCase(name)
            } else {
                MutableStateFlow(emptyList())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun selectGenre(name: String) {
        selectedGenreName.value = name
    }

    fun playSong(songs: List<Song>, index: Int) {
        melodyPlayer.setQueue(songs, index)
    }
}
