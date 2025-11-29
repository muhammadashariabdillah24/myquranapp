package com.myquranapp.core.utils

import android.content.Context
import android.util.Log
import com.myquranapp.core.data.source.local.entity.AudioDownloadEntity
import com.myquranapp.core.data.source.local.room.AudioDownloadDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class AudioManager(
    private val context: Context,
    private val audioDownloadDao: AudioDownloadDao
) {
    companion object {
        private const val TAG = "AudioManager"
        private const val AUDIO_DIR = "quran_audio"
        private const val MAX_RETRIES = 3
        private const val RETRY_DELAY_MS = 1000L
    }

    private val audioDirectory: File by lazy {
        File(context.filesDir, AUDIO_DIR).apply {
            if (!exists()) mkdirs()
        }
    }

    /**
     * Get local audio file path if exists, otherwise return remote URL
     */
    suspend fun getAudioPath(audioUrl: String): String {
        val downloadedAudio = audioDownloadDao.getAudioDownload(audioUrl)
        return if (downloadedAudio?.isDownloaded == true) {
            val localFile = File(downloadedAudio.localFilePath)
            if (localFile.exists()) {
                downloadedAudio.localFilePath
            } else {
                // File deleted, re-download
                audioUrl
            }
        } else {
            audioUrl
        }
    }

    /**
     * Download audio file and save to internal storage
     */
    suspend fun downloadAudio(
        audioUrl: String,
        surahSequence: Int,
        ayahSequence: Int,
        onProgress: ((Int, Int) -> Unit)? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        var retryCount = 0
        var lastException: Exception? = null

        while (retryCount < MAX_RETRIES) {
            try {
                val fileName = generateFileName(surahSequence, ayahSequence)
                val localFile = File(audioDirectory, fileName)

                // Skip if already downloaded
                if (localFile.exists()) {
                    updateDownloadStatus(audioUrl, localFile.absolutePath, surahSequence, ayahSequence, true)
                    return@withContext Result.success(localFile.absolutePath)
                }

                // Download from URL
                val url = URL(audioUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    throw Exception("Server returned HTTP ${connection.responseCode}")
                }

                val totalSize = connection.contentLength
                var downloadedSize = 0

                connection.inputStream.use { input ->
                    FileOutputStream(localFile).use { output ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int

                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            downloadedSize += bytesRead
                            onProgress?.invoke(downloadedSize, totalSize)
                        }
                    }
                }

                // Update database
                updateDownloadStatus(audioUrl, localFile.absolutePath, surahSequence, ayahSequence, true)
                
                Log.d(TAG, "Downloaded: $fileName ($surahSequence:$ayahSequence)")
                return@withContext Result.success(localFile.absolutePath)

            } catch (e: Exception) {
                lastException = e
                retryCount++
                Log.e(TAG, "Download failed (attempt $retryCount/$MAX_RETRIES): ${e.message}")
                
                if (retryCount < MAX_RETRIES) {
                    kotlinx.coroutines.delay(RETRY_DELAY_MS * retryCount)
                }
            }
        }

        // All retries failed
        Result.failure(lastException ?: Exception("Download failed after $MAX_RETRIES attempts"))
    }

    /**
     * Generate consistent filename for audio
     */
    private fun generateFileName(surahSequence: Int, ayahSequence: Int): String {
        return String.format("%03d_%03d.mp3", surahSequence, ayahSequence)
    }

    /**
     * Update download status in database
     */
    private suspend fun updateDownloadStatus(
        audioUrl: String,
        localFilePath: String,
        surahSequence: Int,
        ayahSequence: Int,
        isDownloaded: Boolean
    ) {
        val entity = AudioDownloadEntity(
            audioUrl = audioUrl,
            localFilePath = localFilePath,
            surahSequence = surahSequence,
            ayahSequence = ayahSequence,
            downloadedAt = System.currentTimeMillis(),
            isDownloaded = isDownloaded
        )
        audioDownloadDao.insertAudioDownload(entity)
    }

    /**
     * Get download progress
     */
    fun getDownloadProgress(): Flow<Pair<Int, Int>> {
        return kotlinx.coroutines.flow.combine(
            audioDownloadDao.getDownloadedCount(),
            audioDownloadDao.getTotalCount()
        ) { downloaded, total ->
            Pair(downloaded, total)
        }
    }

    /**
     * Check if audio is downloaded
     */
    suspend fun isAudioDownloaded(audioUrl: String): Boolean {
        val download = audioDownloadDao.getAudioDownload(audioUrl)
        return download?.isDownloaded == true && File(download.localFilePath).exists()
    }

    /**
     * Get total storage used by audio files
     */
    fun getTotalStorageUsed(): Long {
        return audioDirectory.walkTopDown()
            .filter { it.isFile }
            .map { it.length() }
            .sum()
    }

    /**
     * Clear all downloaded audio files
     */
    suspend fun clearAllAudio() = withContext(Dispatchers.IO) {
        audioDirectory.deleteRecursively()
        audioDirectory.mkdirs()
        audioDownloadDao.deleteAllAudioDownloads()
    }
}
