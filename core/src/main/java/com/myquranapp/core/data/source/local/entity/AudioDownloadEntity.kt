package com.myquranapp.core.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio_downloads")
data class AudioDownloadEntity(
    @PrimaryKey
    val audioUrl: String,
    val localFilePath: String,
    val surahSequence: Int,
    val ayahSequence: Int,
    val downloadedAt: Long = System.currentTimeMillis(),
    val isDownloaded: Boolean = false
)
