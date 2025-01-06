package com.example.playlistmaker.media.di

import com.example.playlistmaker.media.presentation.view_model.PlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playlistModule = module {

    viewModel { (currentPlaylistId: Int) ->
        PlaylistViewModel(currentPlaylistId, get(), get(), get())
    }
}