package com.example.playlistmaker.media.di

import com.example.playlistmaker.media.presentation.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playlistsModule = module {
    viewModel {
        PlaylistsViewModel()
    }
}