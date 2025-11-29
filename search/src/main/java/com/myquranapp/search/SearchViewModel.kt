package com.myquranapp.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myquranapp.core.domain.model.Surah
import com.myquranapp.core.domain.usecase.QuranUseCase
import com.myquranapp.core.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class SearchViewModel(
    private val quranUseCase: QuranUseCase
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<Surah>>(emptyList())
    val searchResults: StateFlow<List<Surah>> = _searchResults.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    private val _isFuzzySearch = MutableStateFlow(true)
    private val searchHistoryList = mutableListOf<String>()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private var isDataLoaded = false
    private var currentQuery: String = "" // Track current search query
    
    init {
        // Ensure data is loaded from API to database only if not loaded yet
        ensureDataLoaded()
    }
    
    private fun ensureDataLoaded() {
        if (isDataLoaded) return
        
        viewModelScope.launch {
            _isLoading.value = true
            // Take only SUCCESS or ERROR state, skip intermediate emissions
            quranUseCase.getAllSurahs()
                .filter { it is Resource.Success || it is Resource.Error }
                .take(1)
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _isLoading.value = false
                            isDataLoaded = true
                        }
                        is Resource.Error -> {
                            _isLoading.value = false
                            isDataLoaded = true
                        }
                        is Resource.Loading -> {
                            // Skip loading state
                        }
                    }
                }
        }
    }

    fun searchSurahs(query: String) {
        currentQuery = query // Store current query
        
        if (query.isEmpty()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            if (_isFuzzySearch.value) {
                // MENDEKATI: Use fuzzy search from database (LIKE query)
                // This will return all surahs containing the query string
                quranUseCase.searchSurahs(query).collect { surahs ->
                    _searchResults.value = surahs
                }
            } else {
                // SPESIFIK: Exact match only
                // First get all surahs from database, then filter for exact match
                quranUseCase.searchSurahs(query).collect { surahs ->
                    _searchResults.value = surahs.filter { surah ->
                        // Exact match (case-insensitive) for any of these fields
                        surah.name.latinShort.equals(query, ignoreCase = true) ||
                        surah.name.latinLong.equals(query, ignoreCase = true) ||
                        surah.name.arabicShort.equals(query, ignoreCase = true) ||
                        surah.name.arabicLong.equals(query, ignoreCase = true) ||
                        surah.translation.equals(query, ignoreCase = true)
                    }
                }
            }
        }
    }
    
    fun clearSearch() {
        _searchResults.value = emptyList()
    }

    fun setSearchMode(isFuzzy: Boolean) {
        _isFuzzySearch.value = isFuzzy
        // Re-search with current query when mode changes
        if (currentQuery.isNotEmpty()) {
            searchSurahs(currentQuery)
        }
    }

    fun addToHistory(query: String) {
        if (query.isNotBlank() && !searchHistoryList.contains(query)) {
            searchHistoryList.add(0, query)
            if (searchHistoryList.size > 20) {
                searchHistoryList.removeAt(searchHistoryList.size - 1)
            }
            _searchHistory.value = searchHistoryList.toList()
        }
    }

    fun removeFromHistory(query: String) {
        searchHistoryList.remove(query)
        _searchHistory.value = searchHistoryList.toList()
    }

    fun toggleFavorite(surah: Surah, isFavorite: Boolean) {
        viewModelScope.launch {
            quranUseCase.setFavoriteSurah(surah, isFavorite)
        }
    }
}