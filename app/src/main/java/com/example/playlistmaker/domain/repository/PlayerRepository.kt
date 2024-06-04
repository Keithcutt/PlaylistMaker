package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.api.OnPlayerStateChangeListener

interface PlayerRepository {
    fun preparePlayer(url: String)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun playbackControl()
    fun setOnPlayerStateChangeListener(listener: OnPlayerStateChangeListener)
    fun getCurrentPosition() : Int
}