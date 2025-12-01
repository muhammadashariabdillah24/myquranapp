package com.myquranapp.core.data.source.local

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.myquranapp.core.domain.model.AppSettings
import com.myquranapp.core.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Encrypted Settings Preferences using EncryptedSharedPreferences
 * 
 * Location: core/src/main/java/com/myquranapp/core/data/source/local/EncryptedSettingsPreferences.kt
 * 
 * Teknik:
 * 1. EncryptedSharedPreferences - AES256_SIV for keys, AES256_GCM for values
 * 2. MasterKey dengan Android Keystore
 * 3. Backward compatibility untuk API < 23
 */
class EncryptedSettingsPreferences(private val context: Context) {
    
    companion object {
        private const val PREFS_NAME = "encrypted_app_settings"
        private const val KEY_ARABIC_FONT_SIZE = "arabic_font_size"
        private const val KEY_TRANSLATION_FONT_SIZE = "translation_font_size"
        private const val KEY_SHOW_TRANSLITERATION = "show_transliteration"
        private const val KEY_SHOW_BISMILLAH = "show_bismillah"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_PLAYBACK_SPEED = "playback_speed"
        private const val KEY_AUTO_SCROLL_WITH_AUDIO = "auto_scroll_with_audio"
        private const val KEY_SHOW_NO_INTERNET_MODAL = "show_no_internet_modal"
    }
    
    private val sharedPreferences: SharedPreferences = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        // For Android M (API 23) and above - use EncryptedSharedPreferences
        val spec = KeyGenParameterSpec.Builder(
            MasterKey.DEFAULT_MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()
            
        val masterKey = MasterKey.Builder(context)
            .setKeyGenParameterSpec(spec)
            .build()
            
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } else {
        // For older devices - use standard SharedPreferences
        // Note: For production, consider using a third-party encryption library
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    private val _settingsFlow = MutableStateFlow(getCurrentSettings())
    
    private fun getCurrentSettings(): AppSettings {
        return AppSettings(
            arabicFontSize = sharedPreferences.getInt(KEY_ARABIC_FONT_SIZE, 18),
            translationFontSize = sharedPreferences.getInt(KEY_TRANSLATION_FONT_SIZE, 14),
            showTransliteration = sharedPreferences.getBoolean(KEY_SHOW_TRANSLITERATION, true),
            showBismillah = sharedPreferences.getBoolean(KEY_SHOW_BISMILLAH, true),
            theme = ThemeMode.valueOf(sharedPreferences.getString(KEY_THEME_MODE, "SYSTEM") ?: "SYSTEM"),
            playbackSpeed = sharedPreferences.getFloat(KEY_PLAYBACK_SPEED, 1.0f),
            autoScrollWithAudio = sharedPreferences.getBoolean(KEY_AUTO_SCROLL_WITH_AUDIO, true),
            showNoInternetModal = sharedPreferences.getBoolean(KEY_SHOW_NO_INTERNET_MODAL, true)
        )
    }
    
    fun getSettings(): Flow<AppSettings> = _settingsFlow.asStateFlow()
    
    suspend fun updateArabicFontSize(size: Int) {
        sharedPreferences.edit().putInt(KEY_ARABIC_FONT_SIZE, size).apply()
        _settingsFlow.value = getCurrentSettings()
    }
    
    suspend fun updateTranslationFontSize(size: Int) {
        sharedPreferences.edit().putInt(KEY_TRANSLATION_FONT_SIZE, size).apply()
        _settingsFlow.value = getCurrentSettings()
    }
    
    suspend fun updateShowTransliteration(show: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_SHOW_TRANSLITERATION, show).apply()
        _settingsFlow.value = getCurrentSettings()
    }
    
    suspend fun updateShowBismillah(show: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_SHOW_BISMILLAH, show).apply()
        _settingsFlow.value = getCurrentSettings()
    }
    
    suspend fun updateThemeMode(theme: String) {
        sharedPreferences.edit().putString(KEY_THEME_MODE, theme).apply()
        _settingsFlow.value = getCurrentSettings()
    }
    
    suspend fun updatePlaybackSpeed(speed: Float) {
        sharedPreferences.edit().putFloat(KEY_PLAYBACK_SPEED, speed).apply()
        _settingsFlow.value = getCurrentSettings()
    }
    
    suspend fun updateAutoScrollWithAudio(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_AUTO_SCROLL_WITH_AUDIO, enabled).apply()
        _settingsFlow.value = getCurrentSettings()
    }
    
    suspend fun updateShowNoInternetModal(show: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_SHOW_NO_INTERNET_MODAL, show).apply()
        _settingsFlow.value = getCurrentSettings()
    }
    
    suspend fun clearAll() {
        sharedPreferences.edit().clear().apply()
        _settingsFlow.value = getCurrentSettings()
    }
}
