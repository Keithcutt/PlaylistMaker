package com.example.playlistmaker.media.presentation.state

import com.example.playlistmaker.search.domain.models.Track

sealed interface PlaylistScreenState {
    data object EmptyPlaylist : PlaylistScreenState
    class PlaylistWithTracks(val tracksFromPlaylist: List<Track>) : PlaylistScreenState
}