package com.example.playlistmaker.media.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.FavouritesInteractor
import com.example.playlistmaker.media.presentation.state.FavouritesScreenState
import kotlinx.coroutines.launch

class FavouritesViewModel(private val favouritesInteractor: FavouritesInteractor) : ViewModel() {

    private val _favouritesScreenState = MutableLiveData<FavouritesScreenState>()
    val favouritesScreenState = _favouritesScreenState

    init {
        getFavourites()
    }

    private fun getFavourites() {
        viewModelScope.launch {
            favouritesInteractor.favouriteTracks().collect { tracks ->

                if (tracks.isEmpty()) {
                    favouritesScreenState.postValue(
                        FavouritesScreenState.EmptyScreen
                    )
                } else {
                    favouritesScreenState.postValue(
                        FavouritesScreenState.Content(tracks)
                    )
                }
            }
        }
    }
}