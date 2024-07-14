package com.example.playlistmaker.search.presentation.mapper

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.model.BriefTrackInfo
import java.text.SimpleDateFormat
import java.util.Locale

object BriefTrackInfoMapper {
    fun map(track: Track): BriefTrackInfo {
        return BriefTrackInfo(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime),
            artworkUrl100 = track.artworkUrl100
        )
    }
}