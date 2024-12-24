package com.example.playlistmaker.media.presentation.state

import com.example.playlistmaker.media.presentation.model.PlaylistUIModel

sealed interface PlaylistsScreenState {
    data object Placeholder : PlaylistsScreenState
    class Content(val playlists: List<PlaylistUIModel>) : PlaylistsScreenState
}