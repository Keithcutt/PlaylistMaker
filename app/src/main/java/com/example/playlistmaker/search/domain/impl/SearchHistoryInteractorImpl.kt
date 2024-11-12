package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchHistoryInteractorImpl(private val searchHistoryRepository: SearchHistoryRepository) : SearchHistoryInteractor {
    override fun save(track: Track) {
        searchHistoryRepository.save(track)
    }

    override fun clear() {
        searchHistoryRepository.clear()
    }

    override fun getSearchHistory(): Flow<List<Track>> = flow {
        searchHistoryRepository.getSearchHistory().collect{ tracks ->
            emit(tracks)
        }
    }

    override fun isSearchHistoryNotEmpty(): Boolean {
        return searchHistoryRepository.isSearchHistoryNotEmpty()
    }
}