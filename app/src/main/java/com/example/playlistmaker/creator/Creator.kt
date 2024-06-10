package com.example.playlistmaker.creator

import android.content.Intent
import android.media.MediaPlayer
import com.example.playlistmaker.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.PlayerRepository
import com.example.playlistmaker.domain.use_case.PlayerInteractor
import com.google.gson.Gson

object Creator {
    private const val TRACK_KEY = "track"

    fun provideInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(providePlayerRepository())
    }

    private fun providePlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl(MediaPlayer())
    }

    fun provideCurrentTrack(intent: Intent): Track {
        val json = intent.getStringExtra(TRACK_KEY)
        return Gson().fromJson(json, Track::class.java)
    }
}
