package com.melody.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getThemeMode(): Flow<String> // "light", "dark", "system"
    suspend fun setThemeMode(mode: String)
    
    fun isDynamicColorEnabled(): Flow<Boolean>
    suspend fun setDynamicColorEnabled(enabled: Boolean)
    
    fun getExcludedFolders(): Flow<Set<String>>
    suspend fun addExcludedFolder(path: String)
    suspend fun removeExcludedFolder(path: String)
    
    fun getPinnedFolders(): Flow<Set<String>>
    suspend fun addPinnedFolder(path: String)
    suspend fun removePinnedFolder(path: String)

    fun getPlaybackSpeed(): Flow<Float>
    suspend fun setPlaybackSpeed(speed: Float)

    fun getSleepTimerRemaining(): Flow<Long> // Milliseconds remaining, 0 if inactive
    suspend fun startSleepTimer(durationMs: Long)
    suspend fun stopSleepTimer()

    suspend fun backupSettings(destinationPath: String): Boolean
    suspend fun restoreSettings(sourcePath: String): Boolean

    fun getAppColor(): Flow<Long?> // ARGB packed as Long, null means use default theme
    suspend fun setAppColor(color: Long?)
}
