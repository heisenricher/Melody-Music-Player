package com.melody.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melody.domain.model.Song
import com.melody.domain.usecase.GetMostPlayedUseCase
import com.melody.player.controller.MelodyPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MostPlayedViewModel @Inject constructor(
    getMostPlayedUseCase: GetMostPlayedUseCase,
    private val melodyPlayer: MelodyPlayer
) : ViewModel() {

    val state: StateFlow<MostPlayedUiState> = getMostPlayedUseCase(limit = 50)
        .map { songs ->
            if (songs.isEmpty()) MostPlayedUiState.Empty
            else MostPlayedUiState.Success(songs)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MostPlayedUiState.Loading
        )

    val playerState = melodyPlayer.playerState

    fun playSong(songs: List<Song>, index: Int) {
        melodyPlayer.setQueue(songs, index)
    }
}

sealed interface MostPlayedUiState {
    object Loading : MostPlayedUiState
    object Empty : MostPlayedUiState
    data class Success(val songs: List<Song>) : MostPlayedUiState
}
