package com.myquranapp.core.data

import com.myquranapp.core.data.source.local.LocalDataSource
import com.myquranapp.core.data.source.remote.ApiResponse
import com.myquranapp.core.data.source.remote.RemoteDataSource
import com.myquranapp.core.domain.model.Surah
import com.myquranapp.core.domain.model.SurahDetail
import com.myquranapp.core.domain.repository.IQuranRepository
import com.myquranapp.core.utils.DataMapper
import com.myquranapp.core.utils.Resource
import kotlinx.coroutines.flow.*

class QuranRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : IQuranRepository {
    
    override fun getAllSurahs(): Flow<Resource<List<Surah>>> = flow {
        // 1. Emit Loading state
        emit(Resource.Loading)
        
        // 2. Emit data from database first (cache-first strategy)
        val localData = localDataSource.getAllSurahs().first()
        if (localData.isNotEmpty()) {
            emit(Resource.Success(DataMapper.mapSurahEntitiesToDomain(localData)))
        }
        
        // 3. Fetch from network in background - handle flow cancellation properly
        try {
            when (val apiResponse = remoteDataSource.getAllSurahs().first()) {
                is ApiResponse.Success -> {
                    val surahList = DataMapper.mapSurahResponseToDomain(apiResponse.data)
                    val surahEntities = surahList.map { DataMapper.mapSurahDomainToEntity(it) }
                    localDataSource.deleteNonFavoriteSurahs()
                    localDataSource.insertSurahs(surahEntities)
                    // 4. Emit updated data from database
                    emitAll(
                        localDataSource.getAllSurahs().map {
                            Resource.Success(DataMapper.mapSurahEntitiesToDomain(it))
                        }
                    )
                }
                is ApiResponse.Empty -> {
                    // If API returns empty but we have local data, keep showing it
                    if (localData.isEmpty()) {
                        emit(Resource.Error("Data is empty"))
                    }
                }
                is ApiResponse.Error -> {
                    // If API fails but we have local data, keep showing it
                    if (localData.isEmpty()) {
                        emit(Resource.Error(apiResponse.errorMessage))
                    }
                }
            }
        } catch (e: Exception) {
            // Catch flow cancellation exceptions (including IllegalStateException from Flow transparency violations)
            when {
                e is kotlinx.coroutines.CancellationException -> {
                    // Flow was cancelled - this is expected, ignore it
                }
                e is IllegalStateException && e.message?.contains("Flow exception transparency is violated") == true -> {
                    // Flow transparency violation due to cancellation - ignore it
                }
                else -> {
                    // Other exceptions should be re-thrown
                    throw e
                }
            }
        }
    }
    
    override fun getSurahDetail(surahNumber: Int): Flow<Resource<SurahDetail>> = flow {
        emit(Resource.Loading)
        
        // Handle flow cancellation properly
        try {
            when (val apiResponse = remoteDataSource.getSurahDetail(surahNumber).first()) {
                is ApiResponse.Success -> {
                    val surahDetail = DataMapper.mapSurahDetailResponseToDomain(apiResponse.data)
                    emit(Resource.Success(surahDetail))
                }
                is ApiResponse.Empty -> {
                    emit(Resource.Error("Data is empty"))
                }
                is ApiResponse.Error -> {
                    emit(Resource.Error(apiResponse.errorMessage))
                }
            }
        } catch (e: Exception) {
            // Catch flow cancellation exceptions (including IllegalStateException from Flow transparency violations)
            when {
                e is kotlinx.coroutines.CancellationException -> {
                    // Flow was cancelled - this is expected, ignore it
                }
                e is IllegalStateException && e.message?.contains("Flow exception transparency is violated") == true -> {
                    // Flow transparency violation due to cancellation - ignore it
                }
                else -> {
                    // Other exceptions should be re-thrown
                    throw e
                }
            }
        }
    }
    
    override fun searchSurahs(query: String): Flow<List<Surah>> {
        return localDataSource.searchSurahs(query).map {
            DataMapper.mapSurahEntitiesToDomain(it)
        }
    }
    
    override fun getFavoriteSurahs(): Flow<List<Surah>> {
        return localDataSource.getFavoriteSurahs().map {
            DataMapper.mapSurahEntitiesToDomain(it)
        }
    }
    
    override suspend fun setFavoriteSurah(surah: Surah, isFavorite: Boolean) {
        // Use updateFavoriteStatus to only update the favorite flag
        // This prevents overwriting existing data when toggling favorite
        localDataSource.updateFavoriteStatus(surah.sequence, isFavorite)
    }
    
    override fun isSurahFavorite(surahNumber: Int): Flow<Boolean> {
        return localDataSource.isSurahFavorite(surahNumber)
    }
}
