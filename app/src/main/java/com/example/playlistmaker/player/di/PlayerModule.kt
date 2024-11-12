package com.example.playlistmaker.player.di

import android.media.MediaPlayer
import com.example.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.player.domain.repository.PlayerRepository
import com.example.playlistmaker.player.presentation.view_model.PlayerViewModel
import com.example.playlistmaker.player.presentation.mapper.TrackMapper
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {

    factory { Gson() }

    factory { TrackMapper(get()) }
    factory { MediaPlayer() }

    factory<PlayerRepository> { PlayerRepositoryImpl(get()) }
    factory<PlayerInteractor> { PlayerInteractorImpl(get()) }

    viewModel { (track: Track) ->
        PlayerViewModel(track, get(), get())
    }
}