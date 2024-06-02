package com.example.playlistmaker.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.domain.repository.PlayerRepository

class PlayerRepositoryImpl : PlayerRepository {

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()

    // Нужно подготовить плеер, т.е. передать в него URL нужного трека
    override fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
            // binding.playButton.isEnabled = true
        }

        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            // binding.playButton.setImageResource(R.drawable.btn_play)
            // handler.removeCallbacks(playbackRunnable)
            // binding.playbackProgress.text = getString(R.string.zeroZero)
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        // binding.playButton.setImageResource(R.drawable.btn_pause)
        // playbackProgressCounter()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        // handler.removeCallbacks(playbackRunnable)
        // binding.playButton.setImageResource(R.drawable.btn_play)
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}