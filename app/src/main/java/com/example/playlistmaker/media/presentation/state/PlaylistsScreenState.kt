package com.example.playlistmaker.media.presentation.state

import com.example.playlistmaker.media.domain.model.Playlist

sealed interface PlaylistsScreenState {
    data object Placeholder : PlaylistsScreenState
    class Content(val playlists: List<Playlist>) : PlaylistsScreenState
}