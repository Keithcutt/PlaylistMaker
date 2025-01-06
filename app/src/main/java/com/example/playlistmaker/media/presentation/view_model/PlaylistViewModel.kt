package com.example.playlistmaker.media.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db_api.PlaylistsInteractor
import com.example.playlistmaker.media.presentation.mapper.PlaylistUIMapper
import com.example.playlistmaker.media.presentation.model.PlaylistUIModel
import com.example.playlistmaker.media.presentation.state.PlaylistScreenState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor
import com.example.playlistmaker.util.single_live_event.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class PlaylistViewModel(
    private val currentPlaylistId: Int,
    private val playlistsInteractor: PlaylistsInteractor,
    private val playlistUIMapper: PlaylistUIMapper,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val _playlistDetails = MutableLiveData<PlaylistUIModel>()
    val playlistDetails: LiveData<PlaylistUIModel> = _playlistDetails

    private val _playlistDuration = MutableLiveData<Int>()
    val playlistDuration: LiveData<Int> = _playlistDuration

    private val _playlistScreenState = MutableLiveData<PlaylistScreenState>()
    val playlistScreenState: LiveData<PlaylistScreenState> = _playlistScreenState

    private val _noTracksToShare = SingleLiveEvent<Boolean>()
    val noTracksToShare: LiveData<Boolean> = _noTracksToShare

    init {
        refreshPlaylistDetails(currentPlaylistId)
        getPlaylistDuration(currentPlaylistId)
        getTracksFromPlaylist(currentPlaylistId)
    }

    fun getTracksFromPlaylist(playlistId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.getTracksFromPlaylist(playlistId).collect { tracks ->
                if (tracks.isNotEmpty()) {
                    _playlistScreenState.postValue(
                        PlaylistScreenState.PlaylistWithTracks(tracks)
                    )
                } else {
                    _playlistScreenState.postValue(
                        PlaylistScreenState.EmptyPlaylist
                    )
                }
            }
        }
    }

    fun removeTrackFromPlaylist(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.removeTrackFromPlaylist(track, currentPlaylistId)
                .collect { tracks ->
                    if (tracks.isNotEmpty()) {
                        _playlistScreenState.postValue(
                            PlaylistScreenState.PlaylistWithTracks(tracks)
                        )
                    } else {
                        _playlistScreenState.postValue(
                            PlaylistScreenState.EmptyPlaylist
                        )
                    }
                }

            val playlistBeforeUpdate = playlistDetails.value
            _playlistDetails.postValue(
                playlistBeforeUpdate?.copy(trackCount = playlistBeforeUpdate.trackCount - 1)
            )

            getPlaylistDuration(currentPlaylistId)
        }
    }

    fun sharePlaylist(playlistId: Int) {
        when (playlistScreenState.value) {
            is PlaylistScreenState.EmptyPlaylist -> {
                _noTracksToShare.postValue(true)
            }

            is PlaylistScreenState.PlaylistWithTracks -> {

                viewModelScope.launch(Dispatchers.IO) {
                    val playlistForSharing = playlistsInteractor.getPlaylistById(playlistId).first()
                    val tracksFromPlaylistForSharing =
                        playlistsInteractor.getTracksFromPlaylist(playlistId).first()

                    sharingInteractor.sharePlaylist(
                        playlistForSharing,
                        tracksFromPlaylistForSharing
                    )
                }
            }

            else -> {}
        }
    }

    fun deletePlaylist(playlistId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.deletePlaylist(playlistId)
        }
    }

    fun refreshPlaylistDetails(playlistId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.getPlaylistById(playlistId).collect { playlist ->
                _playlistDetails.postValue(
                    playlistUIMapper.map(playlist)
                )
            }
        }
    }

    private fun getPlaylistDuration(playlistId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.getTracksFromPlaylist(playlistId).collect { tracks ->
                val tracksDuration = tracks.sumOf { track -> track.trackTime }
                val durationFormatted =
                    SimpleDateFormat("mm", Locale.getDefault()).format(tracksDuration).toInt()
                _playlistDuration.postValue(durationFormatted)
            }
        }
    }
}