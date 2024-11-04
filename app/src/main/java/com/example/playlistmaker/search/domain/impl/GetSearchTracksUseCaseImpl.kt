package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.Resource
import com.example.playlistmaker.search.domain.api.GetSearchTracksUseCase
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetSearchTracksUseCaseImpl(private val repository: TracksRepository) :
    GetSearchTracksUseCase {

    override fun execute(expression: String): Flow<Pair<List<Track>?, String?>> {

        return repository.searchTracks(expression).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.value, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }
}