package com.myquranapp.core.data

import com.myquranapp.core.data.source.local.SettingsDataStore
import com.myquranapp.core.domain.model.AppSettings
import com.myquranapp.core.domain.repository.ISettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepository(
    private val settingsDataStore: SettingsDataStore
) : ISettingsRepository {
    
    override fun getSettings(): Flow<AppSettings> {
        return settingsDataStore.getSettings()
    }
    
    override suspend fun updateArabicFontSize(size: Int) {
        settingsDataStore.updateArabicFontSize(size)
    }
    
    override suspend fun updateTranslationFontSize(size: Int) {
        settingsDataStore.updateTranslationFontSize(size)
    }
    
    override suspend fun updateShowTransliteration(show: Boolean) {
        settingsDataStore.updateShowTransliteration(show)
    }
    
    override suspend fun updateShowBismillah(show: Boolean) {
        settingsDataStore.updateShowBismillah(show)
    }
    
    override suspend fun updateTheme(theme: String) {
        settingsDataStore.updateThemeMode(theme)
    }
    
    override suspend fun updatePlaybackSpeed(speed: Float) {
        settingsDataStore.updatePlaybackSpeed(speed)
    }
    
    override suspend fun updateAutoScrollWithAudio(enabled: Boolean) {
        settingsDataStore.updateAutoScrollWithAudio(enabled)
    }
    
    override suspend fun updateShowNoInternetModal(show: Boolean) {
        settingsDataStore.updateShowNoInternetModal(show)
    }
    
    override suspend fun clearAllSettings() {
        settingsDataStore.clearAll()
    }
}
