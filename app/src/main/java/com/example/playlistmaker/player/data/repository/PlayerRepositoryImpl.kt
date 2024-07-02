package com.example.playlistmaker.player.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.repository.OnPlayerStateChangeListener
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.player.domain.repository.PlayerRepository

class PlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : PlayerRepository {

    private var playerState = PlayerState.DEFAULT
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