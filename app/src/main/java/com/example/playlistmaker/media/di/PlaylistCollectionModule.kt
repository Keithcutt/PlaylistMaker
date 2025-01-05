package com.example.playlistmaker.media.di

import com.example.playlistmaker.media.presentation.mapper.PlaylistUIMapper
import com.example.playlistmaker.media.presentation.view_model.PlaylistCollectionViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playlistCollectionModule = module {

    single<PlaylistUIMapper> {
        PlaylistUIMapper(get())
    }

    viewModel {
        PlaylistCollectionViewModel(get(), get())
    }
}