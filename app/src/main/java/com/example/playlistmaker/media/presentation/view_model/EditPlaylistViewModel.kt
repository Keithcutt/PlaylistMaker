package com.example.playlistmaker.media.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db_api.PlaylistsInteractor
import com.example.playlistmaker.media.domain.local_storage_api.LocalStorageInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.presentation.mapper.PlaylistUIMapper
import com.example.playlistmaker.media.presentation.model.PlaylistUIModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val currentPlaylistId: Int,
    private val playlistsInteractor: PlaylistsInteractor,
    localStorageInteractor: LocalStorageInteractor,
    private val playlistUIMapper: PlaylistUIMapper
) : NewPlaylistViewModel(playlistsInteractor, localStorageInteractor) {

    private val _playlistDetails = MutableLiveData<PlaylistUIModel>()
    val playlistDetails: LiveData<PlaylistUIModel> = _playlistDetails

    private lateinit var receivedPlaylist: Playlist


    init {
        getPlaylistById()
    }

    fun updatePlaylist() {
        viewModelScope.launch(Dispatchers.IO) {

            if (playlistCoverFileName == null || receivedPlaylist.coverFileName == playlistCoverFileName) {
                playlistsInteractor.updatePlaylist(
                    receivedPlaylist.copy(
                        playlistName = playlistName,
                        description = playlistDescription,
                    )
                )
            } else {
                playlistsInteractor.updatePlaylist(
                    receivedPlaylist.copy(
                        playlistName = playlistName,
                        description = playlistDescription,
                        coverFileName = playlistCoverFileName
                    )
                )
            }
        }
    }

    private fun getPlaylistById() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.getPlaylistById(currentPlaylistId).collect { playlist ->

                receivedPlaylist = playlist

                _playlistDetails.postValue(
                    playlistUIMapper.map(playlist)
                )
            }
        }
    }
}