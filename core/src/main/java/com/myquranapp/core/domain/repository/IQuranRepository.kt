package com.myquranapp.core.domain.repository

import com.myquranapp.core.domain.model.Surah
import com.myquranapp.core.domain.model.SurahDetail
import com.myquranapp.core.utils.Resource
import kotlinx.coroutines.flow.Flow

interface IQuranRepository {
    fun getAllSurahs(): Flow<Resource<List<Surah>>>
    
    fun getSurahDetail(surahNumber: Int): Flow<Resource<SurahDetail>>
    
    fun searchSurahs(query: String): Flow<List<Surah>>
    
    fun getFavoriteSurahs(): Flow<List<Surah>>
    
    suspend fun setFavoriteSurah(surah: Surah, isFavorite: Boolean)
    
    fun isSurahFavorite(surahNumber: Int): Flow<Boolean>
}
