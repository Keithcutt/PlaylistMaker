package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.domain.local_storage_api.LocalStorageRepository
import com.example.playlistmaker.media.domain.local_storage_api.LocalStorageInteractor

class LocalStorageInteractorImpl(private val localStorageRepository: LocalStorageRepository) : LocalStorageInteractor {
    override fun savePlaylistCover(uri: String): String {
        return localStorageRepository.savePlaylistCover(uri)
    }

    override fun getPlaylistCover(fileName: String): String {
        return localStorageRepository.getPlaylistCover(fileName)
    }
}