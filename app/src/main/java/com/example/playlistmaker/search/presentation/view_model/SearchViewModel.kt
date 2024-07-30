package com.example.playlistmaker.search.presentation.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.api.GetSearchTracksUseCase
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.state.SearchScreenState

class SearchViewModel : ViewModel() {

    companion object{
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val sharedPreferences = Creator.provideSharedPreferences()
    private val searchHistoryInteractor = Creator.provideSearchHistoryInteractor(sharedPreferences)
    private val getSearchTracksUseCase = Creator.provideGetSearchTracksUseCase()

    private var searchQueryText: String? = null

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchQuery(searchQueryText) }

    private val _searchScreenState = MutableLiveData<SearchScreenState>()
    val searchScreenState: LiveData<SearchScreenState> = _searchScreenState

    // Будет открывать плеер для нажатого трека + добавлять трек в историю
    // private val onTrackClickEvent = SingleEventLiveData<Track>()



    private fun searchQuery(expression: String?) {
        if (expression?.isBlank() == false) expression.let {
            _searchScreenState.value = SearchScreenState.Loading

            getSearchTracksUseCase.execute(it, object : GetSearchTracksUseCase.TracksConsumer{
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    if (foundTracks != null) {
                        _searchScreenState.postValue(SearchScreenState.SearchQueryResults(foundTracks.toMutableList()))
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

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    fun clearSearchHistory() {
        searchHistoryInteractor.clear()
        _searchScreenState.value = SearchScreenState.EmptyScreen
    }

    fun onClickEvent(selectedTrack: Track) {
        searchHistoryInteractor.save(selectedTrack)
        // Где-то тут по идее можно добавить обновление адптера у RecyclerView с историей.
        // Открывать экран плеера (будет добавлено после переработки навигации в приложении)
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
            searchDebounce()
        }
    }
}