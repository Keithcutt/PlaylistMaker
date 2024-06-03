package com.example.playlistmaker.domain.use_case

import com.example.playlistmaker.domain.api.OnPlayerStateChangeListener

interface PlayerInteractor {
    fun preparePlayer(url: String)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun playbackControl()
    fun setOnPlayerStateChangeListener(onStateChangeListener: OnPlayerStateChangeListener)
    fun getCurrentPosition() : Int
}