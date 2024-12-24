package com.example.playlistmaker.player.presentation.state

sealed interface AddingTrackToPlaylistStatus {
    class TrackAdded(val playlistName: String) : AddingTrackToPlaylistStatus
    class TrackAlreadyIn(val playlistName: String) : AddingTrackToPlaylistStatus
}