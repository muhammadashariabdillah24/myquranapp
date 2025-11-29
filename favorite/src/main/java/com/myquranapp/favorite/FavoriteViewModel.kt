package com.myquranapp.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myquranapp.core.domain.model.Surah
import com.myquranapp.core.domain.usecase.QuranUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class FavoriteViewModel(
    quranUseCase: QuranUseCase
) : ViewModel() {

    val favoriteSurahs: StateFlow<List<Surah>> = quranUseCase.getFavoriteSurahs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
