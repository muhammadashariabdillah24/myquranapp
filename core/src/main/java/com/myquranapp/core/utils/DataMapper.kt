package com.myquranapp.core.utils

import com.myquranapp.core.data.source.local.entity.SurahEntity
import com.myquranapp.core.data.source.remote.response.*
import com.myquranapp.core.domain.model.*

object DataMapper {
    
    fun mapSurahResponseToDomain(input: List<SurahItem>): List<Surah> {
        return input.map {
            Surah(
                sequence = it.sequence,
                ayahCount = it.ayahCount,
                type = SurahType(
                    arabic = it.type.arabic,
                    latin = it.type.latin
                ),
                name = SurahName(
                    arabicLong = it.name.arabic.long,
                    arabicShort = it.name.arabic.short,
                    latinLong = it.name.latin.long,
                    latinShort = it.name.latin.short
                ),
                translation = it.translation,
                recitationAudio = it.recitation.audio
            )
        }
    }
    
    fun mapSurahEntitiesToDomain(input: List<SurahEntity>): List<Surah> {
        return input.map {
            Surah(
                sequence = it.sequence,
                ayahCount = it.ayahCount,
                type = SurahType(
                    arabic = it.typeArabic,
                    latin = it.typeLatin
                ),
                name = SurahName(
                    arabicLong = it.nameArabicLong,
                    arabicShort = it.nameArabicShort,
                    latinLong = it.nameLatinLong,
                    latinShort = it.nameLatinShort
                ),
                translation = it.translation,
                recitationAudio = it.recitationAudio
            )
        }
    }
    
    fun mapSurahDomainToEntity(input: Surah, isFavorite: Boolean = false): SurahEntity {
        return SurahEntity(
            sequence = input.sequence,
            ayahCount = input.ayahCount,
            typeArabic = input.type.arabic,
            typeLatin = input.type.latin,
            nameArabicLong = input.name.arabicLong,
            nameArabicShort = input.name.arabicShort,
            nameLatinLong = input.name.latinLong,
            nameLatinShort = input.name.latinShort,
            translation = input.translation,
            recitationAudio = input.recitationAudio,
            isFavorite = isFavorite
        )
    }
    
    fun mapSurahDetailResponseToDomain(input: SurahDetailItem): SurahDetail {
        return SurahDetail(
            sequence = input.sequence,
            ayahCount = input.ayahCount,
            type = SurahType(
                arabic = input.type.arabic,
                latin = input.type.latin
            ),
            name = SurahName(
                arabicLong = input.name.arabic.long,
                arabicShort = input.name.arabic.short,
                latinLong = input.name.latin.long,
                latinShort = input.name.latin.short
            ),
            translation = input.translation,
            preBismillah = Bismillah(
                text = input.preBismillah.text,
                translation = input.preBismillah.translation,
                transliteration = input.preBismillah.transliteration
            ),
            recitationAudio = input.recitation.audio,
            ayahs = input.ayah.map { ayahResponse ->
                Ayah(
                    sequenceQuran = ayahResponse.sequence.quran,
                    sequenceSurah = ayahResponse.sequence.surah,
                    juz = ayahResponse.juz,
                    page = ayahResponse.page,
                    text = ayahResponse.text,
                    transliteration = ayahResponse.transliteration,
                    translation = ayahResponse.translation,
                    recitationAudio = ayahResponse.recitation.audio
                )
            }
        )
    }
}
