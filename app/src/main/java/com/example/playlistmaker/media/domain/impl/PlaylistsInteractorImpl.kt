package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.domain.db_api.PlaylistsInteractor
import com.example.playlistmaker.media.domain.db_api.PlaylistsRepository
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val playlistsRepository: PlaylistsRepository) :
    PlaylistsInteractor {
    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistsRepository.insertPlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getPlaylists()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        playlistsRepository.addTrackToPlaylist(track, playlist)
    }

    override fun getPlaylistById(playlistId: Int): Flow<Playlist> {
        return playlistsRepository.getPlaylistById(playlistId)
    }

    override fun getTracksFromPlaylist(playlistId: Int): Flow<List<Track>> {
        return playlistsRepository.getTracksFromPlaylist(playlistId)
    }

    override suspend fun removeTrackFromPlaylist(track: Track, playlistId: Int): Flow<List<Track>> {
        return playlistsRepository.removeTrackFromPlaylist(track, playlistId)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        playlistsRepository.deletePlaylist(playlistId)
    }
}