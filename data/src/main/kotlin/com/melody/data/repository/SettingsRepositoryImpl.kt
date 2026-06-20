package com.melody.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.melody.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "melody_settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val EXCLUDED_FOLDERS = stringSetPreferencesKey("excluded_folders")
        val PINNED_FOLDERS = stringSetPreferencesKey("pinned_folders")
        val PLAYBACK_SPEED = floatPreferencesKey("playback_speed")
        val APP_COLOR = longPreferencesKey("app_color")
    }

    private val sleepTimerRemaining = MutableStateFlow(0L)

    override fun getThemeMode(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[Keys.THEME_MODE] ?: "system"
        }
    }

    override suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.THEME_MODE] = mode
        }
    }

    override fun isDynamicColorEnabled(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[Keys.DYNAMIC_COLOR] ?: true
        }
    }

    override suspend fun setDynamicColorEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.DYNAMIC_COLOR] = enabled
        }
    }

    override fun getExcludedFolders(): Flow<Set<String>> {
        return context.dataStore.data.map { preferences ->
            preferences[Keys.EXCLUDED_FOLDERS] ?: emptySet()
        }
    }

    override suspend fun addExcludedFolder(path: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[Keys.EXCLUDED_FOLDERS] ?: emptySet()
            preferences[Keys.EXCLUDED_FOLDERS] = current + path
        }
    }

    override suspend fun removeExcludedFolder(path: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[Keys.EXCLUDED_FOLDERS] ?: emptySet()
            preferences[Keys.EXCLUDED_FOLDERS] = current - path
        }
    }

    override fun getPinnedFolders(): Flow<Set<String>> {
        return context.dataStore.data.map { preferences ->
            preferences[Keys.PINNED_FOLDERS] ?: emptySet()
        }
    }

    override suspend fun addPinnedFolder(path: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[Keys.PINNED_FOLDERS] ?: emptySet()
            preferences[Keys.PINNED_FOLDERS] = current + path
        }
    }

    override suspend fun removePinnedFolder(path: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[Keys.PINNED_FOLDERS] ?: emptySet()
            preferences[Keys.PINNED_FOLDERS] = current - path
        }
    }

    override fun getPlaybackSpeed(): Flow<Float> {
        return context.dataStore.data.map { preferences ->
            preferences[Keys.PLAYBACK_SPEED] ?: 1.0f
        }
    }

    override suspend fun setPlaybackSpeed(speed: Float) {
        context.dataStore.edit { preferences ->
            preferences[Keys.PLAYBACK_SPEED] = speed
        }
    }

    override fun getSleepTimerRemaining(): Flow<Long> {
        return sleepTimerRemaining
    }

    override suspend fun startSleepTimer(durationMs: Long) {
        sleepTimerRemaining.value = durationMs
    }

    override suspend fun stopSleepTimer() {
        sleepTimerRemaining.value = 0L
    }

    override suspend fun backupSettings(destinationPath: String): Boolean {
        return try {
            val dbFile = context.getDatabasePath("melody.db")
            if (dbFile.exists()) {
                val destFile = File(destinationPath)
                dbFile.copyTo(destFile, overwrite = true)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun restoreSettings(sourcePath: String): Boolean {
        return try {
            val sourceFile = File(sourcePath)
            if (sourceFile.exists()) {
                val dbFile = context.getDatabasePath("melody.db")
                sourceFile.copyTo(dbFile, overwrite = true)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun getAppColor(): kotlinx.coroutines.flow.Flow<Long?> {
        return context.dataStore.data.map { preferences ->
            preferences[Keys.APP_COLOR]
        }
    }

    override suspend fun setAppColor(color: Long?) {
        context.dataStore.edit { preferences ->
            if (color != null) {
                preferences[Keys.APP_COLOR] = color
            } else {
                preferences.remove(Keys.APP_COLOR)
            }
        }
    }
}
