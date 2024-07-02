package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.Resource
import com.example.playlistmaker.search.domain.models.Track

interface TracksRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
}