package com.example.playlistmaker.media.di

import com.example.playlistmaker.media.presentation.mapper.PlaylistUIMapper
import com.example.playlistmaker.media.presentation.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playlistsModule = module {

    single<PlaylistUIMapper> {
        PlaylistUIMapper(get())
    }

    viewModel {
        PlaylistsViewModel(get(), get())
    }
}