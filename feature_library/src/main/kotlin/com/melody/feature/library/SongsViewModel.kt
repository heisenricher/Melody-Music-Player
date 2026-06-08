package com.melody.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melody.domain.model.Song
import com.melody.domain.repository.ScanStatus
import com.melody.domain.usecase.GetAllSongsUseCase
import com.melody.domain.usecase.ScanMediaUseCase
import com.melody.domain.usecase.ToggleFavoriteUseCase
import com.melody.player.controller.MelodyPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    getAllSongsUseCase: GetAllSongsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val scanMediaUseCase: ScanMediaUseCase,
    private val melodyPlayer: MelodyPlayer
) : ViewModel() {

    private val _sortOrder = MutableStateFlow(SortOrder.TITLE_ASC)
    val sortOrder: StateFlow<SortOrder> = _sortOrder

    private val _scanStatus = MutableStateFlow<ScanStatus>(ScanStatus.Idle)
    val scanStatus: StateFlow<ScanStatus> = _scanStatus

    val songsState: StateFlow<SongsUiState> = combine(
        getAllSongsUseCase(),
        _sortOrder
    ) { songs, sort ->
        if (songs.isEmpty()) {
            SongsUiState.Empty
        } else {
            val sortedList = when (sort) {
                SortOrder.TITLE_ASC -> songs.sortedBy { it.title.lowercase() }
                SortOrder.TITLE_DESC -> songs.sortedByDescending { it.title.lowercase() }
                SortOrder.ARTIST_ASC -> songs.sortedBy { it.artist.lowercase() }
                SortOrder.DATE_ADDED -> songs.sortedByDescending { it.dateAdded }
                SortOrder.DURATION -> songs.sortedByDescending { it.duration }
            }
            SongsUiState.Success(sortedList)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SongsUiState.Loading
    )

    val playerState = melodyPlayer.playerState

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun playSong(songs: List<Song>, index: Int) {
        melodyPlayer.setQueue(songs, index)
    }

    fun toggleFavorite(songId: Long) {
        viewModelScope.launch {
            toggleFavoriteUseCase(songId)
        }
    }

    fun triggerMediaScan() {
        viewModelScope.launch {
            scanMediaUseCase().collect { status ->
                _scanStatus.value = status
            }
        }
    }
}

sealed interface SongsUiState {
    object Loading : SongsUiState
    object Empty : SongsUiState
    data class Success(val songs: List<Song>) : SongsUiState
}

enum class SortOrder {
    TITLE_ASC,
    TITLE_DESC,
    ARTIST_ASC,
    DATE_ADDED,
    DURATION
}
