package com.myquranapp.core.domain.repository

import com.myquranapp.core.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface ISettingsRepository {
    fun getSettings(): Flow<AppSettings>
    suspend fun updateArabicFontSize(size: Int)
    suspend fun updateTranslationFontSize(size: Int)
    suspend fun updateShowTransliteration(show: Boolean)
    suspend fun updateShowBismillah(show: Boolean)
    suspend fun updateTheme(theme: String)
    suspend fun updatePlaybackSpeed(speed: Float)
    suspend fun updateAutoScrollWithAudio(enabled: Boolean)
    suspend fun updateShowNoInternetModal(show: Boolean)
    suspend fun clearAllSettings()
}
