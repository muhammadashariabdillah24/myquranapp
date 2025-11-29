package com.myquranapp.ui.detail

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myquranapp.core.domain.model.Surah
import com.myquranapp.core.domain.model.SurahDetail
import com.myquranapp.core.domain.usecase.QuranUseCase
import com.myquranapp.core.utils.AudioManager
import com.myquranapp.core.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val quranUseCase: QuranUseCase,
    private val audioManager: AudioManager
) : ViewModel() {

    private val _surahDetailState = MutableStateFlow<Resource<SurahDetail>>(Resource.Loading)
    val surahDetailState: StateFlow<Resource<SurahDetail>> = _surahDetailState.asStateFlow()

    private val _currentPlayingUrl = MutableStateFlow<String?>(null)
    val currentPlayingUrl: StateFlow<String?> = _currentPlayingUrl.asStateFlow()
    
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()
    
    private var currentSurah: Surah? = null

    private var mediaPlayer: MediaPlayer? = null

    fun loadSurahDetail(surahNumber: Int) {
        viewModelScope.launch {
            quranUseCase.getSurahDetail(surahNumber).collect { resource ->
                _surahDetailState.value = resource
                
                // Extract Surah info from SurahDetail for favorite functionality
                if (resource is Resource.Success) {
                    resource.data?.let { detail ->
                        currentSurah = Surah(
                            sequence = detail.sequence,
                            ayahCount = detail.ayahCount,
                            type = detail.type,
                            name = detail.name,
                            translation = detail.translation,
                            recitationAudio = detail.recitationAudio
                        )
                    }
                }
            }
        }
        
        // Check if surah is favorite
        viewModelScope.launch {
            quranUseCase.isSurahFavorite(surahNumber).collect { isFav ->
                _isFavorite.value = isFav
            }
        }
    }
    
    fun toggleFavorite() {
        currentSurah?.let { surah ->
            viewModelScope.launch {
                val newFavoriteState = !_isFavorite.value
                quranUseCase.setFavoriteSurah(surah, newFavoriteState)
            }
        }
    }

    fun playAudio(audioUrl: String) {
        viewModelScope.launch {
            try {
                // Stop previous audio if playing
                stopAudio()

                // Get local file path if downloaded, otherwise use remote URL
                val audioPath = audioManager.getAudioPath(audioUrl)

                // Create new MediaPlayer
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(audioPath)
                    prepareAsync()
                    
                    setOnPreparedListener {
                        start()
                        _currentPlayingUrl.value = audioUrl
                    }
                    
                    setOnCompletionListener {
                        _currentPlayingUrl.value = null
                        release()
                        mediaPlayer = null
                    }
                    
                    setOnErrorListener { _, _, _ ->
                        _currentPlayingUrl.value = null
                        release()
                        mediaPlayer = null
                        true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _currentPlayingUrl.value = null
            }
        }
    }

    fun stopAudio() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
        _currentPlayingUrl.value = null
    }

    override fun onCleared() {
        super.onCleared()
        stopAudio()
    }
}
