package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import com.example.playlistmaker.search.data.mapper.TrackMapper
import com.example.playlistmaker.search.domain.Resource
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase
) : TracksRepository {

    companion object {
        private const val NO_INTERNET_CONNECTION = -1
        private const val SUCCESSFUL_RESPONSE = 200
    }

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))

        when (response.resultCode) {
            NO_INTERNET_CONNECTION -> emit(Resource.Error("No internet connection"))

            SUCCESSFUL_RESPONSE -> {

                with(response as TrackSearchResponse) {
                    val data = findFavourites(results.map { TrackMapper.map(it) })
                    emit(Resource.Success(data))
                }
            }


            else -> emit(Resource.Error("Internal server error"))
        }
    }

    private suspend fun findFavourites(tracks: List<Track>): List<Track> {
        val favouriteIDsList = appDatabase.trackDao().getFavouriteIDs()
        return tracks.map { track ->
            track.apply {
                isFavourite = favouriteIDsList.contains(trackId)
            }
        }
    }
}