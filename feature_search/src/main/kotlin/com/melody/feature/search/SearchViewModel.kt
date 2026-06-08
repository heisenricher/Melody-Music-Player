package com.melody.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melody.domain.model.Song
import com.melody.domain.repository.SearchResults
import com.melody.domain.usecase.SearchUseCase
import com.melody.player.controller.MelodyPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val melodyPlayer: MelodyPlayer
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    val searchResultsState: StateFlow<SearchResults> = _query
        .flatMapLatest { text ->
            searchUseCase(text)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SearchResults(emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
        )

    fun onQueryChanged(text: String) {
        _query.value = text
    }

    fun playSong(songs: List<Song>, index: Int) {
        melodyPlayer.setQueue(songs, index)
    }
}
