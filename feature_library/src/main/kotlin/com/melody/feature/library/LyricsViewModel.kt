package com.melody.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melody.domain.model.Song
import com.melody.domain.usecase.GetLyricsUseCase
import com.melody.player.controller.MelodyPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LyricsViewModel @Inject constructor(
    private val getLyricsUseCase: GetLyricsUseCase,
    private val melodyPlayer: MelodyPlayer
) : ViewModel() {

    val playerState = melodyPlayer.playerState

    val lyricsState: StateFlow<String?> = melodyPlayer.playerState
        .flatMapLatest { state ->
            val song = state.currentSong
            if (song != null) {
                getLyricsUseCase(song)
            } else {
                MutableStateFlow(null)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}
