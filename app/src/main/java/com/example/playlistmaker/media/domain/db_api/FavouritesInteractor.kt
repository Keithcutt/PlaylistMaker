package com.example.playlistmaker.media.domain.db_api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavouritesInteractor {
    suspend fun insertTrack(track: Track)
    suspend fun deleteTrack(track: Track)
    fun favouriteTracks(): Flow<List<Track>>
}