package com.example.playlistmaker.player.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.FavouritesInteractor
import com.example.playlistmaker.media.domain.db.PlaylistsInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.player.domain.state.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PlayerViewModel(
    private val currentTrack: Track,
    private val playerInteractor: PlayerInteractor,
    private val favouritesInteractor: FavouritesInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    companion object {
        private const val TIMER_UPDATE_INTERVAL = 500L
    }

    private val _playlistCollection = MutableLiveData<List<Playlist>>(emptyList())
    val playlistCollection = _playlistCollection

    private val _playbackState = MutableLiveData<PlayerState>()
    val playbackState = _playbackState

    private val _playbackProgress = MutableLiveData<Int>()
    val playbackProgress = _playbackProgress

    private val _favouriteStatus = MutableLiveData<Boolean>()
    val favouriteStatus = _favouriteStatus

    private var timerJob: Job? = null

    init {
        playerInteractor.preparePlayer(currentTrack.previewUrl)
        playerInteractor.setOnCompletionListener { inTheEnd() }
        _favouriteStatus.value = currentTrack.isFavourite
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.releasePlayer()
        timerJob?.cancel()
    }

    fun refreshPlaylistCollection() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.getPlaylists().collect{ playlistCollection ->
                _playlistCollection.postValue(playlistCollection)
            }
        }
    }

    fun onFavouriteClicked() {
        viewModelScope.launch() {
            if (!currentTrack.isFavourite) {
                favouritesInteractor.insertTrack(currentTrack)
                currentTrack.isFavourite = true
            } else {
                favouritesInteractor.deleteTrack(currentTrack)
                currentTrack.isFavourite = false
            }
            _favouriteStatus.value = currentTrack.isFavourite
        }

    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        _playbackState.value = PlayerState.PAUSED
        timerJob?.cancel()
    }

    fun playbackControl() {
        when (playerInteractor.getPlayerState()) {
            PlayerState.PLAYING -> {
                playerInteractor.pausePlayer()
                _playbackState.value = PlayerState.PAUSED
                timerJob?.cancel()
            }

            PlayerState.PREPARED -> {
                playerInteractor.startPlayer()
                playbackProgressCounter()
                _playbackProgress.value = 0
                _playbackState.value = PlayerState.PLAYING
            }

            PlayerState.PAUSED -> {
                playerInteractor.startPlayer()
                playbackProgressCounter()
                _playbackState.value = PlayerState.PLAYING
            }

            else -> {}
        }
    }

    private fun playbackProgressCounter() {
        val state = playerInteractor.getPlayerState()

        timerJob = viewModelScope.launch {
            while (state == PlayerState.PLAYING) {
                delay(TIMER_UPDATE_INTERVAL)
                _playbackProgress.postValue(playerInteractor.getCurrentPosition())
            }
        }
    }

    private fun inTheEnd() {
        _playbackState.value = PlayerState.PREPARED
        _playbackProgress.value = 0
        timerJob?.cancel()
    }
}