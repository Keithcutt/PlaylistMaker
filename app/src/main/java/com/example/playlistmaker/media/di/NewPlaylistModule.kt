package com.example.playlistmaker.media.di

import com.example.playlistmaker.media.data.impl.LocalStorageRepositoryImpl
import com.example.playlistmaker.media.domain.impl.SavePlaylistCoverUseCaseImpl
import com.example.playlistmaker.media.domain.local_storage_api.LocalStorageRepository
import com.example.playlistmaker.media.domain.local_storage_api.SavePlaylistCoverUseCase
import com.example.playlistmaker.media.presentation.view_model.NewPlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val newPlaylistModule = module {

    single<LocalStorageRepository> { LocalStorageRepositoryImpl(get()) }
    single<SavePlaylistCoverUseCase> { SavePlaylistCoverUseCaseImpl(get()) }

    viewModel {
        NewPlaylistViewModel(get(), get())
    }
}