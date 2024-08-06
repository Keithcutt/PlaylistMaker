package com.example.playlistmaker.player.domain.repository

import com.example.playlistmaker.player.domain.state.PlayerState

interface PlayerRepository {

    fun preparePlayer(url: String)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun setOnCompletionListener(listener: OnCompletionListener)
    fun getCurrentPosition(): Int
    fun getPlayerState(): PlayerState
}