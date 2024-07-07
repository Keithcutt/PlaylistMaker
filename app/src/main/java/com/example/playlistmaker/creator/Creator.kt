package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.player.domain.repository.PlayerRepository
import com.example.playlistmaker.player.domain.use_case.PlayerInteractor
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson

object Creator {

    private const val TRACK_KEY = "track"

    // private const val SEARCH_HISTORY_PREFERENCES = "search_history_shared_preferences"
    private const val PLAYLISTMAKER_PREFERENCES = "playlistmaker_preferences"
    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(providePlayerRepository())
    }

    private fun providePlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl(MediaPlayer())
    }

    fun provideCurrentTrack(intent: Intent): Track {
        val json = intent.getStringExtra(TRACK_KEY)
        return Gson().fromJson(json, Track::class.java)
    }


    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE)
    }

    fun provideSearchHistoryInteractor(sp: SharedPreferences): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(provideSearchHistoryRepository(sp))
    }

    private fun provideSearchHistoryRepository(sp: SharedPreferences): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(sp)
    }
}
