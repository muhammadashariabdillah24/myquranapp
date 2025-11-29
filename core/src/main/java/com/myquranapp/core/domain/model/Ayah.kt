package com.myquranapp.core.domain.model

data class Ayah(
    val sequenceQuran: Int,
    val sequenceSurah: Int,
    val juz: Int,
    val page: Int,
    val text: String,
    val transliteration: String,
    val translation: String,
    val recitationAudio: String
)
