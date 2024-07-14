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
import com.example.playlistmaker.search.presentation.utils.SingleEventLiveData

class SearchViewModel : ViewModel() { //Принимать в себя строку (savedInput)

    companion object{
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val sharedPreferences = Creator.provideSharedPreferences()
    private val searchHistoryInteractor = Creator.provideSearchHistoryInteractor(sharedPreferences)

    private val getSearchTracksUseCase = Creator.provideGetSearchTracksUseCase()

    private var tracks: MutableList<Track> = mutableListOf() // Сделать LiveData?

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchQuery() }

    private val _searchScreenState = MutableLiveData<SearchScreenState>()
    val searchScreenState: LiveData<SearchScreenState> = _searchScreenState

    private val onTrackClickEvent = SingleEventLiveData<Track>() // Будет открывать плеер для нажатого трека + добавлять трек в историю

    //var savedInput: String? = null

    private fun searchQuery(expression: String?) {
        if (expression?.isBlank() == false) expression.let {
            // Loading
            _searchScreenState.postValue(SearchScreenState.Loading)

            getSearchTracksUseCase.searchTracks(it, object : GetSearchTracksUseCase.TracksConsumer{
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    handler.post{
                        if (foundTracks != null) {
                            // Searched content
                            tracks.clear()
                            tracks.addAll(foundTracks)
                            _searchScreenState.postValue(SearchScreenState.SearchQueryResults(foundTracks.toMutableList()))
                        }
                        if (errorMessage != null) {
                            // noInternet
                            _searchScreenState.postValue(SearchScreenState.NoInternetError)
//                            showNoInternetConnectionMessage(true)
                        } else if (tracks.isEmpty()) {
                            // NotFound
                            _searchScreenState.postValue(SearchScreenState.NothingFound)
                        }
                    }
                }

            })
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    fun click
}