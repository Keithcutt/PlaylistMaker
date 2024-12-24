package com.example.playlistmaker.media.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db_api.FavouritesInteractor
import com.example.playlistmaker.media.presentation.state.FavouritesScreenState
import kotlinx.coroutines.launch

class FavouritesViewModel(private val favouritesInteractor: FavouritesInteractor) : ViewModel() {

    private val _favouritesScreenState = MutableLiveData<FavouritesScreenState>()
    val favouritesScreenState: LiveData<FavouritesScreenState> = _favouritesScreenState

    init {
        refreshFavourites()
    }

    fun refreshFavourites() {
        viewModelScope.launch {
            favouritesInteractor.favouriteTracks().collect { tracks ->

                if (tracks.isEmpty()) {
                    _favouritesScreenState.postValue(
                        FavouritesScreenState.EmptyScreen
                    )
                } else {
                    _favouritesScreenState.postValue(
                        FavouritesScreenState.Favourites(tracks)
                    )
                }
            }
        }
    }
}