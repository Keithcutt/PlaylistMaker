package com.example.playlistmaker.media.domain.local_storage_api

interface LocalStorageRepository {
    fun savePlaylistCover(uri: String): String
    fun getPlaylistCover(fileName: String): String
}