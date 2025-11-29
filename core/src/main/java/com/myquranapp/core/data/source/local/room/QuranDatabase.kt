package com.myquranapp.core.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.myquranapp.core.data.source.local.entity.AudioDownloadEntity
import com.myquranapp.core.data.source.local.entity.SurahEntity

@Database(
    entities = [SurahEntity::class, AudioDownloadEntity::class],
    version = 2,
    exportSchema = false
)
abstract class QuranDatabase : RoomDatabase() {
    abstract fun surahDao(): SurahDao
    abstract fun audioDownloadDao(): AudioDownloadDao
}
