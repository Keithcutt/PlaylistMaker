package com.example.playlistmaker.player.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db_api.FavouritesInteractor
import com.example.playlistmaker.media.domain.db_api.PlaylistsInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.presentation.mapper.PlaylistUIMapper
import com.example.playlistmaker.media.presentation.model.PlaylistUIModel
import com.example.playlistmaker.player.domain.interactor.PlayerInteractor
import com.example.playlistmaker.player.domain.state.PlayerState
import com.example.playlistmaker.player.presentation.state.AddingTrackToPlaylistStatus
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.single_live_event.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PlayerViewModel(
    private val currentTrack: Track,
    private val playerInteractor: PlayerInteractor,
    private val favouritesInteractor: FavouritesInteractor,
    private val playlistsInteractor: PlaylistsInteractor,
    private val playlistUIMapper: PlaylistUIMapper
) : ViewModel() {

    companion object {
        private const val TIMER_UPDATE_INTERVAL = 500L
    }

    private val _playbackState = MutableLiveData<PlayerState>()
    val playbackState: LiveData<PlayerState> = _playbackState

    private val _playbackProgress = MutableLiveData<Int>()
    val playbackProgress: LiveData<Int> = _playbackProgress

    private val _favouriteStatus = MutableLiveData<Boolean>()
    val favouriteStatus: LiveData<Boolean> = _favouriteStatus

    private val _playlistCollection = MutableLiveData<List<PlaylistUIModel>>(emptyList())
    val playlistCollection: LiveData<List<PlaylistUIModel>> = _playlistCollection

    private val _trackAddedStatus = SingleLiveEvent<AddingTrackToPlaylistStatus>()
    val trackAddedStatus: LiveData<AddingTrackToPlaylistStatus> = _trackAddedStatus

    private var timerJob: Job? = null
    private var currentPlaylistCollection: List<Playlist> = mutableListOf()

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

    fun addTrackToPlaylist(track: Track, playlist: PlaylistUIModel) {
        if (track.trackId in playlist.trackIdsList) {
            _trackAddedStatus.postValue(AddingTrackToPlaylistStatus.TrackAlreadyIn(playlist.playlistName))
        } else {
            viewModelScope.launch {
                val matchingPlaylist =
                    currentPlaylistCollection.find { it.playlistId == playlist.playlistId }
                if (matchingPlaylist != null) {
                    playlistsInteractor.addTrackToPlaylist(track, matchingPlaylist)
                }
            }
            _trackAddedStatus.postValue(AddingTrackToPlaylistStatus.TrackAdded(playlist.playlistName))
        }
    }

    fun refreshPlaylistCollection() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.getPlaylists().collect { playlistCollection ->

                currentPlaylistCollection = playlistCollection

                _playlistCollection.postValue(
                    mapPlaylistCollectionToUIPlaylistCollection(playlistCollection)
                )
            }
        }
    }

    private fun mapPlaylistCollectionToUIPlaylistCollection(playlistCollection: List<Playlist>): List<PlaylistUIModel> {
        return playlistCollection.map { playlist -> playlistUIMapper.map(playlist) }
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