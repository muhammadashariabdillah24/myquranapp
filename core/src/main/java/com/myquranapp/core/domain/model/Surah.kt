package com.myquranapp.core.domain.model

data class Surah(
    val sequence: Int,
    val ayahCount: Int,
    val type: SurahType,
    val name: SurahName,
    val translation: String,
    val recitationAudio: String
)

data class SurahType(
    val arabic: String,
    val latin: String
)

data class SurahName(
    val arabicLong: String,
    val arabicShort: String,
    val latinLong: String,
    val latinShort: String
)
