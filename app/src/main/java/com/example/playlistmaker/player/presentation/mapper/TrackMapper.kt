package com.example.playlistmaker.player.presentation.mapper

import android.content.Intent
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson

class TrackMapper {

    companion object {
        private const val TRACK_KEY = "track"
    }

    fun getFromIntent(intent: Intent): Track {
        val json = intent.getStringExtra(TRACK_KEY)
        return Gson().fromJson(json, Track::class.java)
    }
}