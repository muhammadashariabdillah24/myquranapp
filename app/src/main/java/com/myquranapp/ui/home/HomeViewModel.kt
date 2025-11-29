package com.myquranapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myquranapp.core.domain.model.Surah
import com.myquranapp.core.domain.usecase.QuranUseCase
import com.myquranapp.core.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val quranUseCase: QuranUseCase
) : ViewModel() {

    private val _surahsState = MutableStateFlow<Resource<List<Surah>>>(Resource.Loading)
    val surahsState: StateFlow<Resource<List<Surah>>> = _surahsState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadSurahs()
    }

    fun loadSurahs() {
        viewModelScope.launch {
            quranUseCase.getAllSurahs().collect { resource ->
                _surahsState.value = resource
            }
        }
    }

    fun searchSurahs(query: String) {
        _searchQuery.value = query
        if (query.isEmpty()) {
            loadSurahs()
            return
        }
        
        viewModelScope.launch {
            quranUseCase.searchSurahs(query).collect { surahs ->
                _surahsState.value = Resource.Success(surahs)
            }
        }
    }

    fun toggleFavorite(surah: Surah, isFavorite: Boolean) {
        viewModelScope.launch {
            quranUseCase.setFavoriteSurah(surah, isFavorite)
        }
    }
}
