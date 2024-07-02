package com.example.playlistmaker.search.presentation

import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

object TrackInfoMapper {
    fun map(track: Track): TrackInfo {
        return TrackInfo(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime),
            artworkUrl100 = track.artworkUrl100
        )
    }
}