package com.example.admapp.ui.screens.config

import android.app.Application
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "settings")

data class SettingsUiState(
    val darkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val autoSync: Boolean = false,
    val syncIntervalMinutes: Int = 30
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = application.dataStore
    private val _imageCacheVersion = MutableStateFlow(0)
    val imageCacheVersion: StateFlow<Int> = _imageCacheVersion.asStateFlow()
    fun clearImageCache(context: Context) {
        viewModelScope.launch {
            coil.Coil.imageLoader(context).diskCache?.clear()
            _imageCacheVersion.update { it + 1 }
        }
    }
    companion object {
        val KEY_DARK_MODE          = booleanPreferencesKey("dark_mode")
        val KEY_NOTIFICATIONS      = booleanPreferencesKey("notifications_enabled")
        val KEY_AUTO_SYNC          = booleanPreferencesKey("auto_sync")
    }

    val uiState: StateFlow<SettingsUiState> = dataStore.data
        .map { prefs ->
            SettingsUiState(
                darkMode             = prefs[KEY_DARK_MODE]     ?: false,
                notificationsEnabled = prefs[KEY_NOTIFICATIONS] ?: true,
                autoSync             = prefs[KEY_AUTO_SYNC]     ?: false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState()
        )

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { it[KEY_DARK_MODE] = enabled }
        }
    }

    fun setNotifications(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { it[KEY_NOTIFICATIONS] = enabled }
        }
    }

    fun setAutoSync(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { it[KEY_AUTO_SYNC] = enabled }
        }
    }
}