package com.example.playlistmaker.media.data.impl

import com.example.playlistmaker.media.data.convertor.TrackConvertor
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.entity.TrackEntity
import com.example.playlistmaker.media.domain.db.FavouritesRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavouritesRepositoryImpl(
    private val appDatabase: AppDatabase
) : FavouritesRepository {

    override suspend fun insertTrack(track: Track) {
        appDatabase.trackDao().insertTrack(
            convertToTrackEntity(track)
        )
    }

    override suspend fun deleteTrack(track: Track) {
        appDatabase.trackDao().deleteTrack(
            convertToTrackEntity(track)
        )
    }

    override fun favouriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getFavouriteTracks()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> TrackConvertor.map(track) }
    }

    private fun convertToTrackEntity(track: Track): TrackEntity {
        return TrackConvertor.map(track)
    }
}