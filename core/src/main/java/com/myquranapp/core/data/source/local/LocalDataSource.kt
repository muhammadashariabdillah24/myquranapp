package com.myquranapp.core.data.source.local

import com.myquranapp.core.data.source.local.entity.SurahEntity
import com.myquranapp.core.data.source.local.room.SurahDao
import kotlinx.coroutines.flow.Flow

class LocalDataSource(private val surahDao: SurahDao) {
    
    fun getAllSurahs(): Flow<List<SurahEntity>> = surahDao.getAllSurahs()
    
    fun getFavoriteSurahs(): Flow<List<SurahEntity>> = surahDao.getFavoriteSurahs()
    
    fun searchSurahs(query: String): Flow<List<SurahEntity>> = surahDao.searchSurahs(query)
    
    fun isSurahFavorite(surahNumber: Int): Flow<Boolean> = surahDao.isSurahFavorite(surahNumber)
    
    suspend fun insertSurahs(surahs: List<SurahEntity>) = surahDao.insertSurahs(surahs)
    
    suspend fun updateSurah(surah: SurahEntity) = surahDao.updateSurah(surah)
    
    suspend fun updateFavoriteStatus(surahNumber: Int, isFavorite: Boolean) = 
        surahDao.updateFavoriteStatus(surahNumber, isFavorite)
    
    suspend fun deleteNonFavoriteSurahs() = surahDao.deleteNonFavoriteSurahs()
}
