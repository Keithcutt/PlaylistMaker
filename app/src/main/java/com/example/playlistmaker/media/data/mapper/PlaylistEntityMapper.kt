package com.example.playlistmaker.media.data.mapper

import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.model.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistEntityMapper(private val gson: Gson) {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            description = playlist.description,
            coverFileName = playlist.coverFileName,
            trackIdsJson = gson.toJson(playlist.trackIdsList),
            trackCount = playlist.trackCount
        )
    }

    fun map(entity: PlaylistEntity): Playlist {
        return Playlist(
            playlistId = entity.playlistId,
            playlistName = entity.playlistName,
            description = entity.description,
            coverFileName = entity.coverFileName,
            trackIdsList = gson.fromJson(
                entity.trackIdsJson,
                object : TypeToken<List<Int>>() {}.type
            ),
            trackCount = entity.trackCount
        )
    }
}