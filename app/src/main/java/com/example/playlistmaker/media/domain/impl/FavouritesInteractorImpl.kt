package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.domain.db_api.FavouritesInteractor
import com.example.playlistmaker.media.domain.db_api.FavouritesRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavouritesInteractorImpl(
    private val favouritesRepository: FavouritesRepository
) : FavouritesInteractor {
    override suspend fun insertTrack(track: Track) {
        favouritesRepository.insertTrack(track)
    }

    override suspend fun deleteTrack(track: Track) {
        favouritesRepository.deleteTrack(track)
        track.isFavourite = false
    }

    override fun favouriteTracks(): Flow<List<Track>> {
        return favouritesRepository.favouriteTracks()
    }

}