package com.example.playlistmaker.search.presentation.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.api.GetSearchTracksUseCase
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.state.SearchScreenState

class SearchViewModel(private val searchHistoryInteractor: SearchHistoryInteractor,
                      private val getSearchTracksUseCase: GetSearchTracksUseCase) : ViewModel() {

    companion object{
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var searchQueryText: String? = null

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchQuery(searchQueryText) }

    private val _searchScreenState = MutableLiveData<SearchScreenState>()
    val searchScreenState: LiveData<SearchScreenState> = _searchScreenState




    private fun searchQuery(expression: String?) {
        if (expression?.isBlank() == false) expression.let {
            _searchScreenState.value = SearchScreenState.Loading

            getSearchTracksUseCase.execute(it, object : GetSearchTracksUseCase.TracksConsumer{
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    if (foundTracks != null) {
                        _searchScreenState.postValue(SearchScreenState.SearchQueryResults(foundTracks))
                    }
                    if (errorMessage != null) {
                        _searchScreenState.postValue(SearchScreenState.NoInternetError)
                    } else if (foundTracks?.isEmpty() == true) {
                        _searchScreenState.postValue(SearchScreenState.NothingFound)
                    }
                }

            })
        }
    }

    fun repeatSearchQuery() {
        handler.removeCallbacks(searchRunnable)
        searchQuery(searchQueryText)
    }

    private fun executeSearchWithDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
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
                _searchScreenState.value = SearchScreenState.SearchHistory(searchHistoryInteractor.getSearchHistory())
            } else {
                _searchScreenState.value = SearchScreenState.EmptyScreen
            }
        } else if (input?.isBlank() == true) {
            _searchScreenState.value = SearchScreenState.EmptyScreen
        } else {
            executeSearchWithDebounce()
        }
    }
}