package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface GetSearchTracksUseCase {

    fun execute(expression: String): Flow<Pair<List<Track>?, String?>>
}