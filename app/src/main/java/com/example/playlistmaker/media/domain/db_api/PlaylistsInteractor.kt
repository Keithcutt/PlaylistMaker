package com.example.playlistmaker.media.domain.db_api

import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun insertPlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)
}