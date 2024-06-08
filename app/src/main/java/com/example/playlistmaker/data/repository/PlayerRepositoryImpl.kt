package com.example.playlistmaker.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.domain.repository.OnPlayerStateChangeListener
import com.example.playlistmaker.domain.models.PlayerState
import com.example.playlistmaker.domain.repository.PlayerRepository

class PlayerRepositoryImpl() : PlayerRepository {

    private var playerState = PlayerState.DEFAULT
    private var mediaPlayer = MediaPlayer()
    private lateinit var onStateChangeListener : OnPlayerStateChangeListener

    override fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            onStateChangeListener.onChange(playerState)
            playerState = PlayerState.PREPARED
        }

        mediaPlayer.setOnCompletionListener {
            playerState = PlayerState.PREPARED
            onStateChangeListener.onChange(playerState)
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = PlayerState.PLAYING
        onStateChangeListener.onChange(playerState)
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = PlayerState.PAUSED
        onStateChangeListener.onChange(playerState)
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition() : Int {
        return mediaPlayer.currentPosition
    }

    override fun setOnPlayerStateChangeListener(listener: OnPlayerStateChangeListener) {
        onStateChangeListener = listener
    }

    override fun getPlayerState(): PlayerState {
        return playerState
    }
}