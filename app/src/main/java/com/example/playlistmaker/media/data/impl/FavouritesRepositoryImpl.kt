package com.example.playlistmaker.media.data.impl

import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.entity.FavouriteTrackEntity
import com.example.playlistmaker.media.data.mapper.FavouriteTrackEntityMapper
import com.example.playlistmaker.media.domain.db_api.FavouritesRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavouritesRepositoryImpl(
    private val appDatabase: AppDatabase
) : FavouritesRepository {

    override suspend fun insertTrack(track: Track) {
        appDatabase.favouriteTrackDao().insertTrack(
            convertToTrackEntity(track)
        )
    }

    override suspend fun deleteTrack(track: Track) {
        appDatabase.favouriteTrackDao().deleteTrack(
            convertToTrackEntity(track)
        )
    }

    override fun favouriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.favouriteTrackDao().getFavouriteTracks()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<FavouriteTrackEntity>): List<Track> {
        return tracks.map { track -> FavouriteTrackEntityMapper.map(track) }
    }

    private fun convertToTrackEntity(track: Track): FavouriteTrackEntity {
        return FavouriteTrackEntityMapper.map(track)
    }
}