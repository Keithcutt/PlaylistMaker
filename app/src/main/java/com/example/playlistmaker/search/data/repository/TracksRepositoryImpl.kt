package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import com.example.playlistmaker.search.data.mapper.TrackMapper
import com.example.playlistmaker.search.domain.Resource
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    companion object {
        private const val NO_INTERNET_CONNECTION = -1
        private const val SUCCESSFUL_RESPONSE = 200
    }

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))

        when (response.resultCode) {
            NO_INTERNET_CONNECTION -> emit(Resource.Error("No internet connection"))

            SUCCESSFUL_RESPONSE -> emit(Resource.Success((response as TrackSearchResponse).results.map {
                TrackMapper.map(it)
            }))

            else -> emit(Resource.Error("Internal server error"))
        }
    }
}