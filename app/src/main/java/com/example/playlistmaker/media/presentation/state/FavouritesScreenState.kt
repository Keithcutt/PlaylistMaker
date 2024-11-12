package com.example.playlistmaker.media.presentation.state

import com.example.playlistmaker.search.domain.models.Track

sealed interface FavouritesScreenState {
    data object EmptyScreen : FavouritesScreenState
    class Favourites(val favouriteTracks: List<Track>) : FavouritesScreenState
}