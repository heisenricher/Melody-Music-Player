package com.melody.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melody.domain.model.Song
import com.melody.domain.usecase.UpdateSongTagsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagEditorViewModel @Inject constructor(
    private val updateSongTagsUseCase: UpdateSongTagsUseCase
) : ViewModel() {

    private val _editStatus = MutableStateFlow<TagEditStatus>(TagEditStatus.Idle)
    val editStatus: StateFlow<TagEditStatus> = _editStatus

    fun updateTags(
        song: Song,
        title: String,
        artist: String,
        album: String,
        genre: String,
        artworkPath: String?
    ) {
        viewModelScope.launch {
            _editStatus.value = TagEditStatus.Saving
            val success = updateSongTagsUseCase(song.id, title, artist, album, genre, artworkPath)
            if (success) {
                _editStatus.value = TagEditStatus.Success
            } else {
                _editStatus.value = TagEditStatus.Error("Failed to edit tags. File may be read-only or invalid.")
            }
        }
    }

    fun resetStatus() {
        _editStatus.value = TagEditStatus.Idle
    }
}

sealed interface TagEditStatus {
    object Idle : TagEditStatus
    object Saving : TagEditStatus
    object Success : TagEditStatus
    data class Error(val message: String) : TagEditStatus
}
