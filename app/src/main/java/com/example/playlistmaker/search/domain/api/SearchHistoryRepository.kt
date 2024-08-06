package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryRepository {
    fun save(track: Track)
    fun clear()
    fun getSearchHistory(): MutableList<Track>
    fun isSearchHistoryNotEmpty(): Boolean
}