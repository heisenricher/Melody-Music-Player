package com.melody.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melody.domain.model.FolderNode
import com.melody.domain.model.Song
import com.melody.domain.usecase.GetFoldersUseCase
import com.melody.domain.usecase.GetSongsByFolderUseCase
import com.melody.domain.usecase.PinFolderUseCase
import com.melody.domain.usecase.ExcludeFolderUseCase
import com.melody.player.controller.MelodyPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoldersViewModel @Inject constructor(
    getFoldersUseCase: GetFoldersUseCase,
    private val getSongsByFolderUseCase: GetSongsByFolderUseCase,
    private val pinFolderUseCase: PinFolderUseCase,
    private val excludeFolderUseCase: ExcludeFolderUseCase,
    private val melodyPlayer: MelodyPlayer
) : ViewModel() {

    val foldersState: StateFlow<List<FolderNode>> = getFoldersUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val selectedFolderPath = MutableStateFlow<String?>(null)

    val folderSongsState: StateFlow<List<Song>> = selectedFolderPath
        .flatMapLatest { path ->
            if (path != null) {
                getSongsByFolderUseCase(path)
            } else {
                MutableStateFlow(emptyList())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun selectFolder(path: String) {
        selectedFolderPath.value = path
    }

    fun pinFolder(path: String, pin: Boolean) {
        viewModelScope.launch {
            pinFolderUseCase(path, pin)
        }
    }

    fun excludeFolder(path: String, exclude: Boolean) {
        viewModelScope.launch {
            excludeFolderUseCase(path, exclude)
        }
    }

    fun playSong(songs: List<Song>, index: Int) {
        melodyPlayer.setQueue(songs, index)
    }
}
