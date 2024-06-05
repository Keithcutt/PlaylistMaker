package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.api.OnPlayerStateChangeListener

interface PlayerRepository {

    fun setUrl(url: String)
    fun preparePlayer()
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    // fun playbackControl()
    fun setOnPlayerStateChangeListener(listener: OnPlayerStateChangeListener)
    fun getCurrentPosition() : Int
}