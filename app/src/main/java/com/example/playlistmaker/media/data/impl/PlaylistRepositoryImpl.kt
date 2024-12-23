package com.example.playlistmaker.media.data.impl

import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.data.mapper.PlaylistEntityMapper
import com.example.playlistmaker.media.domain.db.PlaylistsRepository
import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistEntityMapper: PlaylistEntityMapper
) : PlaylistsRepository {
    override suspend fun insertPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(
            playlistEntityMapper.map(playlist)
        )
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().updatePlaylist(
            playlistEntityMapper.map(playlist)
        )
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(convertToPlaylists(playlists))
    }

    private fun convertToPlaylists(playlistEntities: List<PlaylistEntity>): List<Playlist> {
        return playlistEntities.map { playlistEntity -> playlistEntityMapper.map(playlistEntity) }
    }
}