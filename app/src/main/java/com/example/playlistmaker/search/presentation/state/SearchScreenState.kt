package com.example.playlistmaker.search.presentation.state

import com.example.playlistmaker.search.domain.models.Track

sealed interface SearchScreenState{
    object Loading: SearchScreenState
    class SearchQueryResults(val foundTracks: MutableList<Track>): SearchScreenState // foundTracks: MutableList<TrackInfo>
    class SearchHistory(val searchedTracks: MutableList<Track>): SearchScreenState // searchedTracks: MutableList<TrackInfo>
    object NoInternetError: SearchScreenState
    object NothingFound: SearchScreenState // Мб надо будет сделать чтобы принимал в себя пустой список треков

    // object EmptyScreen: SearchScreenState
}
