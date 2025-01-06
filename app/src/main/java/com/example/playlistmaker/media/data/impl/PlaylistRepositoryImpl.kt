package com.example.playlistmaker.media.data.impl

import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.data.mapper.PlaylistEntityMapper
import com.example.playlistmaker.media.data.mapper.PlaylistTrackEntityMapper
import com.example.playlistmaker.media.domain.db_api.PlaylistsRepository
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistEntityMapper: PlaylistEntityMapper,
) : PlaylistsRepository {
    override suspend fun insertPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(
            playlistEntityMapper.map(playlist)
        )
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(convertToPlaylists(playlists))
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {

        appDatabase.playlistDao().updatePlaylist(
            playlistEntityMapper.map(
                playlist.apply {
                    trackIdsList += track.trackId
                    trackCount += 1
                }
            )
        )

        appDatabase.playlistTrackDao().insertTrack(
            PlaylistTrackEntityMapper.map(track)
        )
    }

    override fun getPlaylistById(playlistId: Int): Flow<Playlist> = flow {
        val playlist = appDatabase.playlistDao().getPlaylistById(playlistId)
        emit(
            playlistEntityMapper.map(playlist)
        )
    }

    override fun getTracksFromPlaylist(playlistId: Int): Flow<List<Track>> = flow {
        val playlist = playlistEntityMapper.map(
            appDatabase.playlistDao().getPlaylistById(playlistId)
        )
        val tracksFromAllPlaylists = appDatabase.playlistTrackDao().getTracksFromAllPlaylists()
        val tracksFromPlaylist: MutableList<Track> = mutableListOf()

        playlist.trackIdsList.forEach { trackId ->
            tracksFromAllPlaylists.forEach { trackEntity ->
                if (trackId == trackEntity.trackId) {
                    tracksFromPlaylist.add(
                        PlaylistTrackEntityMapper.map(trackEntity)
                    )
                }
            }
        }

        tracksFromPlaylist.reverse()
        emit(tracksFromPlaylist)
    }

    override suspend fun removeTrackFromPlaylist(track: Track, playlistId: Int): Flow<List<Track>> =
        flow {
            val trackId = track.trackId
            val playlist = playlistEntityMapper.map(
                appDatabase.playlistDao().getPlaylistById(playlistId)
            )
            val updatedTrackIdsList = playlist.trackIdsList.filterNot { it == trackId }
            val updatedPlaylist = playlist.copy(
                trackIdsList = updatedTrackIdsList,
                trackCount = playlist.trackCount - 1
            )

            appDatabase.playlistDao().updatePlaylist(
                playlistEntityMapper.map(updatedPlaylist)
            )

            if (shouldDeleteTrackFromDB(trackId)) {
                appDatabase.playlistTrackDao().deleteTrackById(trackId)
            }

            emitAll(getTracksFromPlaylist(playlistId))
        }

    override suspend fun deletePlaylist(playlistId: Int) {

        val playlistToDelete = playlistEntityMapper.map(
            appDatabase.playlistDao().getPlaylistById(playlistId)
        )
        val trackIDsFromPlaylist = playlistToDelete.trackIdsList

        appDatabase.playlistDao().deletePlaylist(playlistId)

        trackIDsFromPlaylist.forEach { trackId ->
            if (shouldDeleteTrackFromDB(trackId)) {
                appDatabase.playlistTrackDao().deleteTrackById(trackId)
            }
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().updatePlaylist(
            playlistEntityMapper.map(playlist)
        )
    }

    private suspend fun shouldDeleteTrackFromDB(trackId: Int): Boolean {
        val playlistCollection = convertToPlaylists(appDatabase.playlistDao().getPlaylists())

        playlistCollection.forEach { playlist ->
            if (playlist.trackIdsList.contains(trackId)) {
                return false
            }
        }
        return true
    }

    private fun convertToPlaylists(playlistEntities: List<PlaylistEntity>): List<Playlist> {
        return playlistEntities.map { playlistEntity -> playlistEntityMapper.map(playlistEntity) }
    }
}