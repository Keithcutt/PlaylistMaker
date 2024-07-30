package com.example.playlistmaker.player.presentation.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.state.PlayerState
import com.example.playlistmaker.search.domain.models.Track

class PlayerViewModel(currentTrack: Track) : ViewModel() {

    private val playerInteractor = Creator.providePlayerInteractor()

    private val handler = Handler(Looper.getMainLooper())
    private val playbackRunnable = { playbackProgressCounter() }

    private val _playbackState = MutableLiveData<PlayerState>()
    val playbackState = _playbackState

    private val _playbackProgress = MutableLiveData<Int>()
    val playbackProgress = _playbackProgress


    init {
        playerInteractor.preparePlayer(currentTrack.previewUrl)
        playerInteractor.setOnCompletionListener{ inTheEnd() }
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.releasePlayer()
        handler.removeCallbacks(playbackRunnable)
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        handler.removeCallbacks(playbackRunnable)
    }

    fun playbackControl() {
        when(playerInteractor.getPlayerState()) {
            PlayerState.PLAYING -> {
                playerInteractor.pausePlayer()
                _playbackState.value = PlayerState.PAUSED
                handler.removeCallbacks(playbackRunnable)
            }

            PlayerState.PREPARED, PlayerState.PAUSED -> {
                playerInteractor.startPlayer()
                playbackProgressCounter()
                _playbackProgress.value = 0
                _playbackState.value = PlayerState.PLAYING
            }
            else -> {}
        }
    }

    private fun playbackProgressCounter() {
        val state = playerInteractor.getPlayerState()
        if (state == PlayerState.PLAYING) {
            _playbackProgress.value = playerInteractor.getCurrentPosition()
            handler.postDelayed(playbackRunnable, 300)
        }
    }

    private fun inTheEnd() {
        _playbackState.value = PlayerState.PREPARED
        _playbackProgress.value = 0
        handler.removeCallbacks(playbackRunnable)
    }
}