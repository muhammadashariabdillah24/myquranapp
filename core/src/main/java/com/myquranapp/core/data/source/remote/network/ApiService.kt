package com.myquranapp.core.data.source.remote.network

import com.myquranapp.core.data.source.remote.response.SurahDetailResponse
import com.myquranapp.core.data.source.remote.response.SurahResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    
    @GET("api/v1/surah")
    suspend fun getAllSurahs(): SurahResponse
    
    @GET("api/v1/surah/{surahNumber}")
    suspend fun getSurahDetail(
        @Path("surahNumber") surahNumber: Int
    ): SurahDetailResponse
}
