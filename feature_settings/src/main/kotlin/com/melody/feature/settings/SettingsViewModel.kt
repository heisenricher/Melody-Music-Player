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

    /** Currently selected app primary color as ARGB Long, null = use default theme */
    val appColor: StateFlow<Long?> = settingsRepository.getAppColor()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setAppColor(color: Long?) {
        viewModelScope.launch {
            settingsRepository.setAppColor(color)
        }
    }
}
