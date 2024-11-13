package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class SearchHistoryInteractorImpl(private val searchHistoryRepository: SearchHistoryRepository) :
    SearchHistoryInteractor {
    override fun save(track: Track) {
        searchHistoryRepository.save(track)
    }

    override fun clear() {
        searchHistoryRepository.clear()
    }

    override fun getSearchHistory(): Flow<List<Track>> {
        return searchHistoryRepository.getSearchHistory()
    }

    override fun isSearchHistoryNotEmpty(): Boolean {
        return searchHistoryRepository.isSearchHistoryNotEmpty()
    }
}