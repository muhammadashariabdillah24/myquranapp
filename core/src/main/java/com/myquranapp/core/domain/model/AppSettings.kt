package com.myquranapp.core.domain.model

data class AppSettings(
    val arabicFontSize: Int = 18,
    val translationFontSize: Int = 14,
    val showTransliteration: Boolean = true,
    val showBismillah: Boolean = true,
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val playbackSpeed: Float = 1.0f,
    val autoScrollWithAudio: Boolean = true,
    val showNoInternetModal: Boolean = true
)

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}
