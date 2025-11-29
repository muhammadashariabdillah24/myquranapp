package com.myquranapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myquranapp.core.domain.model.AppSettings
import com.myquranapp.core.domain.usecase.SettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsUseCase: SettingsUseCase
) : ViewModel() {
    
    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            settingsUseCase.getSettings().collect { appSettings ->
                _settings.value = appSettings
            }
        }
    }
    
    fun updateArabicFontSize(size: Int) {
        viewModelScope.launch {
            settingsUseCase.updateArabicFontSize(size)
        }
    }
    
    fun updateTranslationFontSize(size: Int) {
        viewModelScope.launch {
            settingsUseCase.updateTranslationFontSize(size)
        }
    }
    
    fun updateShowTransliteration(show: Boolean) {
        viewModelScope.launch {
            settingsUseCase.updateShowTransliteration(show)
        }
    }
    
    fun updateShowBismillah(show: Boolean) {
        viewModelScope.launch {
            settingsUseCase.updateShowBismillah(show)
        }
    }
    
    fun updateTheme(theme: String) {
        viewModelScope.launch {
            settingsUseCase.updateTheme(theme)
        }
    }
    
    fun updatePlaybackSpeed(speed: Float) {
        viewModelScope.launch {
            settingsUseCase.updatePlaybackSpeed(speed)
        }
    }
    
    fun updateAutoScrollWithAudio(enabled: Boolean) {
        viewModelScope.launch {
            settingsUseCase.updateAutoScrollWithAudio(enabled)
        }
    }
    
    fun updateShowNoInternetModal(show: Boolean) {
        viewModelScope.launch {
            settingsUseCase.updateShowNoInternetModal(show)
        }
    }
    
    fun clearAllSettings() {
        viewModelScope.launch {
            settingsUseCase.clearAllSettings()
        }
    }
}
