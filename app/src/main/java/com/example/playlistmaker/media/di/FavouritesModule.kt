package com.example.playlistmaker.media.di

import com.example.playlistmaker.media.presentation.view_model.FavouritesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val favouritesModule = module {
    viewModel {
        FavouritesViewModel(get())
    }
}