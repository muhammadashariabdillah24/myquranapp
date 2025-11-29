package com.myquranapp.core.domain.usecase

import com.myquranapp.core.domain.model.Surah
import com.myquranapp.core.domain.model.SurahDetail
import com.myquranapp.core.domain.repository.IQuranRepository
import com.myquranapp.core.utils.Resource
import kotlinx.coroutines.flow.Flow

class QuranInteractor(private val quranRepository: IQuranRepository) : QuranUseCase {
    
    override fun getAllSurahs(): Flow<Resource<List<Surah>>> = 
        quranRepository.getAllSurahs()
    
    override fun getSurahDetail(surahNumber: Int): Flow<Resource<SurahDetail>> = 
        quranRepository.getSurahDetail(surahNumber)
    
    override fun searchSurahs(query: String): Flow<List<Surah>> = 
        quranRepository.searchSurahs(query)
    
    override fun getFavoriteSurahs(): Flow<List<Surah>> = 
        quranRepository.getFavoriteSurahs()
    
    override suspend fun setFavoriteSurah(surah: Surah, isFavorite: Boolean) {
        quranRepository.setFavoriteSurah(surah, isFavorite)
    }
    
    override fun isSurahFavorite(surahNumber: Int): Flow<Boolean> = 
        quranRepository.isSurahFavorite(surahNumber)
}
