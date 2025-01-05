package com.example.playlistmaker.media.data.mapper

import com.example.playlistmaker.media.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.search.domain.models.Track

object PlaylistTrackEntityMapper {

    fun map(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
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
            isfavourite = track.isFavourite
        )
    }

    fun map(trackEntity: PlaylistTrackEntity): Track {
        return Track(
            trackId = trackEntity.trackId,
            trackName = trackEntity.trackName,
            artistName = trackEntity.artistName,
            trackTime = trackEntity.trackTime,
            artworkUrl100 = trackEntity.artworkUrl100,
            primaryGenreName = trackEntity.primaryGenreName,
            releaseDate = trackEntity.releaseDate,
            country = trackEntity.country,
            previewUrl = trackEntity.previewUrl,
            collectionName = trackEntity.collectionName,
            isFavourite = trackEntity.isfavourite
        )
    }
}