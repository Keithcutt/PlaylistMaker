package com.example.playlistmaker.search.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.GetSearchTracksUseCase
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.state.SearchScreenState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val getSearchTracksUseCase: GetSearchTracksUseCase
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var searchQueryText: String? = null
    private var searchJob: Job? = null

    private val _searchScreenState = MutableLiveData<SearchScreenState>()
    val searchScreenState: LiveData<SearchScreenState> = _searchScreenState

    private fun searchQuery(expression: String?) {
        if (expression?.isBlank() == false) expression.let {
            _searchScreenState.value = SearchScreenState.Loading

            viewModelScope.launch {
                getSearchTracksUseCase
                    .execute(it)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }

        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {

        if (errorMessage != null) {
            _searchScreenState.postValue(SearchScreenState.NoInternetError)
        } else if (foundTracks != null) {
            if (foundTracks.isEmpty()) {
                _searchScreenState.postValue(SearchScreenState.NothingFound)
            } else {
                _searchScreenState.postValue(
                    SearchScreenState.SearchQueryResults(
                        foundTracks
                    )
                )
            }
        }
    }

    fun searchWithoutDebounce() {
        searchJob?.cancel()
        searchQuery(searchQueryText)
    }

    private fun searchWithDebounce() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchQuery(searchQueryText)
        }
    }

    fun clearSearchHistory() {
        searchHistoryInteractor.clear()
        _searchScreenState.value = SearchScreenState.EmptyScreen
    }

    fun onClickEvent(selectedTrack: Track) {
        searchHistoryInteractor.save(selectedTrack)
    }

    fun onTextChanged(input: String?) {
        searchQueryText = input

        if (input?.isEmpty() == true) {
            if (searchHistoryInteractor.isSearchHistoryNotEmpty()) {
                showSearchHistory()
            } else {
                _searchScreenState.value = SearchScreenState.EmptyScreen
            }
        } else if (input?.isBlank() == true) {
            _searchScreenState.value = SearchScreenState.EmptyScreen
        } else {
            searchWithDebounce()
        }
    }

    private fun showSearchHistory() {
        viewModelScope.launch {
            searchHistoryInteractor.getSearchHistory().collect { tracks ->
                _searchScreenState.value = SearchScreenState.SearchHistory(tracks)
            }
        }
    }

    fun refreshFavourites() {
        viewModelScope.launch {
            searchHistoryInteractor.getSearchHistory().collect { tracks ->
                _searchScreenState.value = SearchScreenState.SearchHistory(tracks)
            }

            searchQuery(searchQueryText)
        }
    }
}