package com.myquranapp.core.data.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.myquranapp.core.domain.model.AppSettings
import com.myquranapp.core.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

class SettingsDataStore(private val context: Context) {
    
    companion object {
        val ARABIC_FONT_SIZE = intPreferencesKey("arabic_font_size")
        val TRANSLATION_FONT_SIZE = intPreferencesKey("translation_font_size")
        val SHOW_TRANSLITERATION = booleanPreferencesKey("show_transliteration")
        val SHOW_BISMILLAH = booleanPreferencesKey("show_bismillah")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val PLAYBACK_SPEED = floatPreferencesKey("playback_speed")
        val AUTO_SCROLL_WITH_AUDIO = booleanPreferencesKey("auto_scroll_with_audio")
        val SHOW_NO_INTERNET_MODAL = booleanPreferencesKey("show_no_internet_modal")
    }
    
    fun getSettings(): Flow<AppSettings> = context.dataStore.data.map { preferences ->
        AppSettings(
            arabicFontSize = preferences[ARABIC_FONT_SIZE] ?: 18,
            translationFontSize = preferences[TRANSLATION_FONT_SIZE] ?: 14,
            showTransliteration = preferences[SHOW_TRANSLITERATION] ?: true,
            showBismillah = preferences[SHOW_BISMILLAH] ?: true,
            theme = ThemeMode.valueOf(preferences[THEME_MODE] ?: "SYSTEM"),
            playbackSpeed = preferences[PLAYBACK_SPEED] ?: 1.0f,
            autoScrollWithAudio = preferences[AUTO_SCROLL_WITH_AUDIO] ?: true,
            showNoInternetModal = preferences[SHOW_NO_INTERNET_MODAL] ?: true
        )
    }
    
    suspend fun updateArabicFontSize(size: Int) {
        context.dataStore.edit { preferences ->
            preferences[ARABIC_FONT_SIZE] = size
        }
    }
    
    suspend fun updateTranslationFontSize(size: Int) {
        context.dataStore.edit { preferences ->
            preferences[TRANSLATION_FONT_SIZE] = size
        }
    }
    
    suspend fun updateShowTransliteration(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_TRANSLITERATION] = show
        }
    }
    
    suspend fun updateShowBismillah(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_BISMILLAH] = show
        }
    }
    
    suspend fun updateThemeMode(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = theme
        }
    }
    
    suspend fun updatePlaybackSpeed(speed: Float) {
        context.dataStore.edit { preferences ->
            preferences[PLAYBACK_SPEED] = speed
        }
    }
    
    suspend fun updateAutoScrollWithAudio(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_SCROLL_WITH_AUDIO] = enabled
        }
    }
    
    suspend fun updateShowNoInternetModal(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_NO_INTERNET_MODAL] = show
        }
    }
    
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
