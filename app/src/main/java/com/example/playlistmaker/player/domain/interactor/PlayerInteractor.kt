package com.example.playlistmaker.player.domain.interactor

import com.example.playlistmaker.player.domain.state.PlayerState
import com.example.playlistmaker.player.domain.repository.OnCompletionListener

interface PlayerInteractor {

    fun preparePlayer(url: String)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun setOnCompletionListener(listener: OnCompletionListener)
    fun getCurrentPosition(): Int
    fun getPlayerState(): PlayerState
}