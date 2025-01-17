package com.example.playlistmaker.media.di

import com.example.playlistmaker.media.data.impl.LocalStorageRepositoryImpl
import com.example.playlistmaker.media.domain.impl.LocalStorageInteractorImpl
import com.example.playlistmaker.media.domain.local_storage_api.LocalStorageInteractor
import com.example.playlistmaker.media.domain.local_storage_api.LocalStorageRepository
import com.example.playlistmaker.media.presentation.view_model.EditPlaylistViewModel
import com.example.playlistmaker.media.presentation.view_model.NewPlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val editPlaylistModule = module {

    single<LocalStorageRepository> { LocalStorageRepositoryImpl(get()) }
    single<LocalStorageInteractor> { LocalStorageInteractorImpl(get()) }

    viewModel {
        NewPlaylistViewModel(get(), get())
    }

    viewModel { (currentPlaylistId: Int) ->
        EditPlaylistViewModel(currentPlaylistId, get(), get(), get())
    }
}