package com.myquranapp.core.utils

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import com.myquranapp.core.data.source.local.room.QuranDatabase
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

/**
 * Database Encryption Helper using SQLCipher
 * 
 * Location: core/src/main/java/com/myquranapp/core/utils/DatabaseEncryption.kt
 * 
 * Teknik:
 * 1. SQLCipher 4.5.4 - AES-256 encryption
 * 2. Passphrase generation dari device-specific identifiers
 * 3. Database factory dengan SupportFactory
 */
object DatabaseEncryption {
    
    private const val DATABASE_NAME = "quran_database.db"
    
    /**
     * Generate passphrase from device-specific info
     * Kombinasi: Build.ID + Build.SERIAL (unique per device)
     */
    private fun generatePassphrase(context: Context): ByteArray {
        val deviceId = android.provider.Settings.Secure.getString(
            context.contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        )
        
        // Combine dengan app signature untuk keamanan tambahan
        val packageInfo = context.packageManager.getPackageInfo(
            context.packageName,
            android.content.pm.PackageManager.GET_SIGNATURES
        )
        val signature = packageInfo.signatures?.getOrNull(0)?.toCharsString() ?: "default-signature"
        
        val combined = "$deviceId-${context.packageName}-$signature"
        return combined.toByteArray()
    }
    
    /**
     * Build encrypted database instance
     */
    fun buildEncryptedDatabase(context: Context): QuranDatabase {
        val passphrase = generatePassphrase(context)
        val factory = SupportFactory(passphrase)
        
        return Room.databaseBuilder(
            context.applicationContext,
            QuranDatabase::class.java,
            DATABASE_NAME
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()
    }
    
    /**
     * Initialize SQLCipher library
     */
    fun initSQLCipher(context: Context) {
        SQLiteDatabase.loadLibs(context)
    }
}
