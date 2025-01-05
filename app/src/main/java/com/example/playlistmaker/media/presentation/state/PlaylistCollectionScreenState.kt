package com.example.playlistmaker.media.presentation.state

import com.example.playlistmaker.media.presentation.model.PlaylistUIModel

sealed interface PlaylistCollectionScreenState {
    data object Placeholder : PlaylistCollectionScreenState
    class Content(val playlists: List<PlaylistUIModel>) : PlaylistCollectionScreenState
}