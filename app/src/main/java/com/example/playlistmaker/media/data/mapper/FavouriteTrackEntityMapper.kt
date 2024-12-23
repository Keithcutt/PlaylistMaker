package com.example.playlistmaker.media.data.mapper

import com.example.playlistmaker.media.data.db.entity.FavouriteTrackEntity
import com.example.playlistmaker.search.domain.models.Track

object FavouriteTrackEntityMapper {

    fun map(track: Track): FavouriteTrackEntity {
        return FavouriteTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            primaryGenreName = track.primaryGenreName,
            releaseDate = track.releaseDate,
            country = track.country,
            previewUrl = track.previewUrl,
            collectionName = track.collectionName,
            timestamp = System.currentTimeMillis()
        )
    }

    fun map(track: FavouriteTrackEntity): Track {
        return Track(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            primaryGenreName = track.primaryGenreName,
            releaseDate = track.releaseDate,
            country = track.country,
            previewUrl = track.previewUrl,
            collectionName = track.collectionName,
            isFavourite = true
        )
    }
}