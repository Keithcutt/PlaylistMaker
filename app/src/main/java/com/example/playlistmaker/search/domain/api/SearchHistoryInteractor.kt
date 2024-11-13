package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryInteractor {
    fun save(track: Track)
    fun clear()
    fun getSearchHistory(): Flow<List<Track>>
    fun isSearchHistoryNotEmpty(): Boolean
}