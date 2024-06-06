package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.api.OnPlayerStateChangeListener
import com.example.playlistmaker.domain.models.PlayerState

interface PlayerRepository {

    fun preparePlayer(url: String)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    // fun playbackControl()
    fun setOnPlayerStateChangeListener(listener: OnPlayerStateChangeListener)
    fun getCurrentPosition() : Int

    fun getPlayerState(): PlayerState
    fun setPlayerState(state: PlayerState)
}