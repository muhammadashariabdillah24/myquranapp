package com.myquranapp.core.data

import com.myquranapp.core.data.source.local.EncryptedSettingsPreferences
import com.myquranapp.core.domain.model.AppSettings
import com.myquranapp.core.domain.repository.ISettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepository(
    private val encryptedSettingsPreferences: EncryptedSettingsPreferences
) : ISettingsRepository {
    
    override fun getSettings(): Flow<AppSettings> {
        return encryptedSettingsPreferences.getSettings()
    }
    
    override suspend fun updateArabicFontSize(size: Int) {
        encryptedSettingsPreferences.updateArabicFontSize(size)
    }
    
    override suspend fun updateTranslationFontSize(size: Int) {
        encryptedSettingsPreferences.updateTranslationFontSize(size)
    }
    
    override suspend fun updateShowTransliteration(show: Boolean) {
        encryptedSettingsPreferences.updateShowTransliteration(show)
    }
    
    override suspend fun updateShowBismillah(show: Boolean) {
        encryptedSettingsPreferences.updateShowBismillah(show)
    }
    
    override suspend fun updateTheme(theme: String) {
        encryptedSettingsPreferences.updateThemeMode(theme)
    }
    
    override suspend fun updatePlaybackSpeed(speed: Float) {
        encryptedSettingsPreferences.updatePlaybackSpeed(speed)
    }
    
    override suspend fun updateAutoScrollWithAudio(enabled: Boolean) {
        encryptedSettingsPreferences.updateAutoScrollWithAudio(enabled)
    }
    
    override suspend fun updateShowNoInternetModal(show: Boolean) {
        encryptedSettingsPreferences.updateShowNoInternetModal(show)
    }
    
    override suspend fun clearAllSettings() {
        encryptedSettingsPreferences.clearAll()
    }
}
