package com.myquranapp.core.domain.usecase

import com.myquranapp.core.domain.model.AppSettings
import com.myquranapp.core.domain.repository.ISettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsUseCase(private val settingsRepository: ISettingsRepository) {
    
    fun getSettings(): Flow<AppSettings> = settingsRepository.getSettings()
    
    suspend fun updateArabicFontSize(size: Int) = 
        settingsRepository.updateArabicFontSize(size)
    
    suspend fun updateTranslationFontSize(size: Int) = 
        settingsRepository.updateTranslationFontSize(size)
    
    suspend fun updateShowTransliteration(show: Boolean) = 
        settingsRepository.updateShowTransliteration(show)
    
    suspend fun updateShowBismillah(show: Boolean) = 
        settingsRepository.updateShowBismillah(show)
    
    suspend fun updateTheme(theme: String) = 
        settingsRepository.updateTheme(theme)
    
    suspend fun updatePlaybackSpeed(speed: Float) = 
        settingsRepository.updatePlaybackSpeed(speed)
    
    suspend fun updateAutoScrollWithAudio(enabled: Boolean) = 
        settingsRepository.updateAutoScrollWithAudio(enabled)
    
    suspend fun updateShowNoInternetModal(show: Boolean) = 
        settingsRepository.updateShowNoInternetModal(show)
    
    suspend fun clearAllSettings() = 
        settingsRepository.clearAllSettings()
}
