package com.myquranapp.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surah")
data class SurahEntity(
    @PrimaryKey
    @ColumnInfo(name = "sequence")
    val sequence: Int,
    
    @ColumnInfo(name = "ayah_count")
    val ayahCount: Int,
    
    @ColumnInfo(name = "type_arabic")
    val typeArabic: String,
    
    @ColumnInfo(name = "type_latin")
    val typeLatin: String,
    
    @ColumnInfo(name = "name_arabic_long")
    val nameArabicLong: String,
    
    @ColumnInfo(name = "name_arabic_short")
    val nameArabicShort: String,
    
    @ColumnInfo(name = "name_latin_long")
    val nameLatinLong: String,
    
    @ColumnInfo(name = "name_latin_short")
    val nameLatinShort: String,
    
    @ColumnInfo(name = "translation")
    val translation: String,
    
    @ColumnInfo(name = "recitation_audio")
    val recitationAudio: String,
    
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false
)
