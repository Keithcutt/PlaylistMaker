package com.example.playlistmaker.player.domain.repository

import com.example.playlistmaker.player.domain.models.PlayerState

interface PlayerRepository {

    fun preparePlayer(url: String)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun setOnPlayerStateChangeListener(listener: OnPlayerStateChangeListener)
    fun getCurrentPosition(): Int
    fun getPlayerState(): PlayerState
}