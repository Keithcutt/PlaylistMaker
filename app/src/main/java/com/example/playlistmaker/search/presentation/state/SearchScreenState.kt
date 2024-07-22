package com.example.playlistmaker.search.presentation.state

import com.example.playlistmaker.search.domain.models.Track

sealed interface SearchScreenState{
    data object Loading: SearchScreenState
    class SearchQueryResults(val foundTracks: MutableList<Track>): SearchScreenState
    class SearchHistory(val searchedTracks: MutableList<Track>): SearchScreenState
    data object NoInternetError: SearchScreenState
    data object NothingFound: SearchScreenState
    data object EmptyScreen: SearchScreenState
}
