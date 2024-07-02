package com.example.playlistmaker.player.domain.use_case

import com.example.playlistmaker.player.domain.repository.OnPlayerStateChangeListener
import com.example.playlistmaker.player.domain.models.PlayerState

interface PlayerInteractor {

    fun preparePlayer(url: String)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun setOnPlayerStateChangeListener(listener: OnPlayerStateChangeListener)
    fun getCurrentPosition() : Int
    fun getPlayerState(): PlayerState
}