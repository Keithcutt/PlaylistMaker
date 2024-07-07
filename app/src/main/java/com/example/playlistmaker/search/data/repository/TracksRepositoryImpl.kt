package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import com.example.playlistmaker.search.data.mapper.TrackMapper
import com.example.playlistmaker.search.domain.Resource
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    companion object{
        private const val NO_INTERNET_CONNECTION = -1
        private const val SUCCESSFUL_RESPONSE = 200
    }

    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))

        return when (response.resultCode) {
            NO_INTERNET_CONNECTION -> Resource.Error("Отсутствует подключение к интернету") // Убрать

            SUCCESSFUL_RESPONSE -> Resource.Success((response as TrackSearchResponse).results.map {
                    TrackMapper.map(it)
                }
            )

            else -> {
                Resource.Error("Internal server error") // Убрать
            }
        }
    }

}