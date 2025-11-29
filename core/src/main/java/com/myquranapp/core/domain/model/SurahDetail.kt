package com.myquranapp.core.domain.model

data class SurahDetail(
    val sequence: Int,
    val ayahCount: Int,
    val type: SurahType,
    val name: SurahName,
    val translation: String,
    val preBismillah: Bismillah,
    val recitationAudio: String,
    val ayahs: List<Ayah>
)

data class Bismillah(
    val text: String,
    val translation: String,
    val transliteration: String
)
