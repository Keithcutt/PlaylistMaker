package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.player.domain.repository.PlayerRepository
import com.example.playlistmaker.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.player.domain.repository.OnCompletionListener

class PlayerInteractorImpl(
    private val playerRepository: PlayerRepository
) : PlayerInteractor {

    override fun preparePlayer(url: String) {
        playerRepository.preparePlayer(url)
    }

    override fun startPlayer() {
        playerRepository.startPlayer()
    }

    override fun pausePlayer() {
        playerRepository.pausePlayer()
    }

    override fun releasePlayer() {
        playerRepository.releasePlayer()
    }

    override fun setOnCompletionListener(listener: OnCompletionListener) {
        playerRepository.setOnCompletionListener(listener)
    }

    override fun getCurrentPosition(): Int {
        return playerRepository.getCurrentPosition()
    }

    override fun getPlayerState(): PlayerState {
        return playerRepository.getPlayerState()
    }
}