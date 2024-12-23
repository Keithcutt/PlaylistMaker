package com.example.playlistmaker.media.domain.local_storage_api

interface SavePlaylistCoverUseCase {
    fun execute(uri: String)
}