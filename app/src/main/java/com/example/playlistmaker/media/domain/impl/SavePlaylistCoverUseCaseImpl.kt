package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.domain.local_storage_api.LocalStorageRepository
import com.example.playlistmaker.media.domain.local_storage_api.SavePlaylistCoverUseCase

class SavePlaylistCoverUseCaseImpl(private val localStorageRepository: LocalStorageRepository) : SavePlaylistCoverUseCase {
    override fun execute(uri: String) {
        localStorageRepository.savePlaylistCover(uri)
    }
}