package com.myquranapp.core.data.source.remote

import android.util.Log
import com.myquranapp.core.data.source.remote.network.ApiService
import com.myquranapp.core.data.source.remote.response.SurahDetailItem
import com.myquranapp.core.data.source.remote.response.SurahItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RemoteDataSource(private val apiService: ApiService) {
    
    suspend fun getAllSurahs(): Flow<ApiResponse<List<SurahItem>>> {
        return flow {
            try {
                val response = apiService.getAllSurahs()
                if (response.success && response.data.isNotEmpty()) {
                    emit(ApiResponse.Success(response.data))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.message.toString()))
                Log.e("RemoteDataSource", e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }
    
    suspend fun getSurahDetail(surahNumber: Int): Flow<ApiResponse<SurahDetailItem>> {
        return flow {
            try {
                val response = apiService.getSurahDetail(surahNumber)
                if (response.success) {
                    emit(ApiResponse.Success(response.data))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.message.toString()))
                Log.e("RemoteDataSource", e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }
}

sealed class ApiResponse<out R> {
    data class Success<out T>(val data: T) : ApiResponse<T>()
    data class Error(val errorMessage: String) : ApiResponse<Nothing>()
    data object Empty : ApiResponse<Nothing>()
}
