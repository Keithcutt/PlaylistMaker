package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface GetSearchTracksUseCase {

    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }
}