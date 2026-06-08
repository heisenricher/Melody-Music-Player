package com.melody.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melody.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val themeMode: StateFlow<String> = settingsRepository.getThemeMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")

    val dynamicColor: StateFlow<Boolean> = settingsRepository.isDynamicColorEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val playbackSpeed: StateFlow<Float> = settingsRepository.getPlaybackSpeed()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1.0f)

    val sleepTimerRemaining: StateFlow<Long> = settingsRepository.getSleepTimerRemaining()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDynamicColorEnabled(enabled)
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        viewModelScope.launch {
            settingsRepository.setPlaybackSpeed(speed)
        }
    }

    fun startSleepTimer(durationMs: Long) {
        viewModelScope.launch {
            settingsRepository.startSleepTimer(durationMs)
        }
    }

    fun stopSleepTimer() {
        viewModelScope.launch {
            settingsRepository.stopSleepTimer()
        }
    }

    fun backupSettings(path: String) {
        viewModelScope.launch {
            settingsRepository.backupSettings(path)
        }
    }

    fun restoreSettings(path: String) {
        viewModelScope.launch {
            settingsRepository.restoreSettings(path)
        }
    }
}
