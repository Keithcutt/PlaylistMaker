package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.domain.api.OnPlayerStateChangeListener
import com.example.playlistmaker.domain.repository.PlayerRepository
import com.example.playlistmaker.domain.use_case.PlayerInteractor

class PlayerInteractorImpl(
    private val playerRepository: PlayerRepository // интерфейс, который будет реализовать медиаплеер (?)
) : PlayerInteractor {

    // Здесь просто выполняем те методы которые есть в репозитории
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

     override fun playbackControl() {
         playerRepository.playbackControl()
    }

    override fun setOnPlayerStateChangeListener(onStateChangeListener: OnPlayerStateChangeListener) {
        playerRepository.setOnPlayerStateChangeListener(onStateChangeListener)
    }

    override fun getCurrentPosition(): Int {
        return playerRepository.getCurrentPosition()
    }
}