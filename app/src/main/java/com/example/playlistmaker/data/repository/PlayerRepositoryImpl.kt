package com.example.playlistmaker.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.OnPlayerStateChangeListener
import com.example.playlistmaker.domain.models.PlayerState
import com.example.playlistmaker.domain.repository.PlayerRepository

class PlayerRepositoryImpl() : PlayerRepository {

    private var playerState = PlayerState.DEFAULT
    private var mediaPlayer = MediaPlayer()
    private lateinit var onStateChangeListener : OnPlayerStateChangeListener

    // Нужно подготовить плеер, т.е. передать в него URL нужного трека
    override fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            playerState = PlayerState.PREPARED
            //вызвать слушатель
            onStateChangeListener.onChange(playerState)

            // binding.playButton.isEnabled = true

        }

        mediaPlayer.setOnCompletionListener {
            playerState = PlayerState.PREPARED

            // binding.playButton.setImageResource(R.drawable.btn_play)
            // handler.removeCallbacks(playbackRunnable)
            // binding.playbackProgress.text = getString(R.string.zeroZero)
            onStateChangeListener.onChange(playerState)
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = PlayerState.PLAYING
        // binding.playButton.setImageResource(R.drawable.btn_pause)
        // playbackProgressCounter()
        onStateChangeListener.onChange(playerState)

    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = PlayerState.PAUSED

        // handler.removeCallbacks(playbackRunnable)
        // binding.playButton.setImageResource(R.drawable.btn_play)
        onStateChangeListener.onChange(playerState)
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun playbackControl() {
        when(playerState) {
            PlayerState.PLAYING -> {
                pausePlayer()

            }
            PlayerState.PREPARED, PlayerState.PAUSED -> {
                startPlayer()
            }
            else -> {}
        }
    }

    override fun getCurrentPosition() : Int {
        return mediaPlayer.currentPosition
    }

    override fun setOnPlayerStateChangeListener(listener: OnPlayerStateChangeListener) {
        onStateChangeListener = listener
    }
}