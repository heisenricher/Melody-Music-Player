package com.melody.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melody.domain.model.PlayerState
import com.melody.domain.usecase.ToggleFavoriteUseCase
import com.melody.player.controller.MelodyPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val melodyPlayer: MelodyPlayer,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    val playerState: StateFlow<PlayerState> = melodyPlayer.playerState

    fun play() = melodyPlayer.play()
    
    fun pause() = melodyPlayer.pause()
    
    fun skipToNext() = melodyPlayer.skipToNext()
    
    fun skipToPrevious() = melodyPlayer.skipToPrevious()
    
    fun seekTo(positionMs: Long) = melodyPlayer.seekTo(positionMs)
    
    fun toggleShuffle() = melodyPlayer.toggleShuffle()
    
    fun toggleRepeat() = melodyPlayer.toggleRepeat()

    fun toggleFavorite(songId: Long) {
        viewModelScope.launch {
            toggleFavoriteUseCase(songId)
        }
    }

    fun setPlaybackSpeed(speed: Float) = melodyPlayer.setPlaybackSpeed(speed)

    fun setBassBoost(strength: Short) = melodyPlayer.setBassBoost(strength)

    fun setVirtualizer(strength: Short) = melodyPlayer.setVirtualizer(strength)
}
