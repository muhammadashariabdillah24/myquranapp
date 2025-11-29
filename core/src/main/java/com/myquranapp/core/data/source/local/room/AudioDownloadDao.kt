package com.myquranapp.core.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.myquranapp.core.data.source.local.entity.AudioDownloadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioDownloadDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudioDownload(audioDownload: AudioDownloadEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudioDownloads(audioDownloads: List<AudioDownloadEntity>)
    
    @Update
    suspend fun updateAudioDownload(audioDownload: AudioDownloadEntity)
    
    @Query("SELECT * FROM audio_downloads WHERE audioUrl = :audioUrl LIMIT 1")
    suspend fun getAudioDownload(audioUrl: String): AudioDownloadEntity?
    
    @Query("SELECT * FROM audio_downloads WHERE isDownloaded = 1")
    fun getAllDownloadedAudios(): Flow<List<AudioDownloadEntity>>
    
    @Query("SELECT * FROM audio_downloads WHERE isDownloaded = 0")
    suspend fun getPendingDownloads(): List<AudioDownloadEntity>
    
    @Query("SELECT COUNT(*) FROM audio_downloads WHERE isDownloaded = 1")
    fun getDownloadedCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM audio_downloads")
    fun getTotalCount(): Flow<Int>
    
    @Query("DELETE FROM audio_downloads")
    suspend fun deleteAllAudioDownloads()
}
