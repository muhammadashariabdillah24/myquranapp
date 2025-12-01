package com.myquranapp.core.data.source.local.room

import androidx.room.*
import com.myquranapp.core.data.source.local.entity.SurahEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SurahDao {
    
    @Query("SELECT * FROM surah ORDER BY sequence ASC")
    fun getAllSurahs(): Flow<List<SurahEntity>>
    
    @Query("SELECT * FROM surah WHERE is_favorite = 1 ORDER BY sequence ASC")
    fun getFavoriteSurahs(): Flow<List<SurahEntity>>
    
    @Query("SELECT * FROM surah WHERE sequence = :surahNumber")
    fun getSurahByNumber(surahNumber: Int): Flow<SurahEntity?>
    
    @Query("SELECT * FROM surah WHERE name_latin_short LIKE '%' || :query || '%' OR name_arabic_short LIKE '%' || :query || '%' OR translation LIKE '%' || :query || '%'")
    fun searchSurahs(query: String): Flow<List<SurahEntity>>
    
    @Query("SELECT COALESCE(is_favorite, 0) FROM surah WHERE sequence = :surahNumber")
    fun isSurahFavorite(surahNumber: Int): Flow<Boolean>
    
    @Query("SELECT is_favorite FROM surah WHERE sequence = :surahNumber")
    suspend fun isSurahFavoriteSync(surahNumber: Int): Boolean?
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSurahs(surahs: List<SurahEntity>)
    
    @Update
    suspend fun updateSurah(surah: SurahEntity)
    
    @Query("UPDATE surah SET is_favorite = :isFavorite WHERE sequence = :surahNumber")
    suspend fun updateFavoriteStatus(surahNumber: Int, isFavorite: Boolean)
    
    @Query("DELETE FROM surah WHERE is_favorite = 0")
    suspend fun deleteNonFavoriteSurahs()
}
