package com.example.playlistmaker.player.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.state.PlayerState
import com.example.playlistmaker.player.domain.repository.OnCompletionListener
import com.example.playlistmaker.player.domain.repository.PlayerRepository

class PlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : PlayerRepository {

    private var playerState = PlayerState.DEFAULT
    private lateinit var onCompletionListener : OnCompletionListener

    override fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = PlayerState.PREPARED
        }

        mediaPlayer.setOnCompletionListener {
            playerState = PlayerState.PREPARED
            onCompletionListener.inTheEnd()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = PlayerState.PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = PlayerState.PAUSED
    }

    override fun releasePlayer() {
        mediaPlayer.release()
        playerState = PlayerState.DEFAULT
    }

    override fun getCurrentPosition() : Int {
        return if (playerState == PlayerState.PLAYING || playerState == PlayerState.PAUSED) {
            mediaPlayer.currentPosition
        } else {
            0
        }
    }

    override fun setOnCompletionListener(listener: OnCompletionListener) {
        onCompletionListener = listener
    }

    override fun getPlayerState(): PlayerState {
        return playerState
    }
}