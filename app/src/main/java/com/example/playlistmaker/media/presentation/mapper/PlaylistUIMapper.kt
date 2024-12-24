package com.example.playlistmaker.media.presentation.mapper

import android.net.Uri
import com.example.playlistmaker.media.domain.local_storage_api.LocalStorageInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.presentation.model.PlaylistUIModel

class PlaylistUIMapper(private val localStorageInteractor: LocalStorageInteractor) {
    fun map(playlist: Playlist): PlaylistUIModel {
        return PlaylistUIModel(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            description = playlist.description,
            coverUri = if (playlist.coverFileName == null) {
                null
            } else {
                Uri.parse(localStorageInteractor.getPlaylistCover(playlist.coverFileName))
            },
            trackIdsList = playlist.trackIdsList,
            trackCount = playlist.trackCount
        )
    }
}