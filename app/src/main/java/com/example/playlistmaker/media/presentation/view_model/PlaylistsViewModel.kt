package com.example.playlistmaker.media.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db_api.PlaylistsInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.presentation.mapper.PlaylistUIMapper
import com.example.playlistmaker.media.presentation.model.PlaylistUIModel
import com.example.playlistmaker.media.presentation.state.PlaylistsScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val playlistUIMapper: PlaylistUIMapper
) : ViewModel() {

    private val _playlistsScreenState = MutableLiveData<PlaylistsScreenState>()
    val playlistsScreenState: LiveData<PlaylistsScreenState> = _playlistsScreenState

    init {
        refreshPlaylists()
    }

    fun refreshPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.getPlaylists().collect { playlistCollection ->

                if (playlistCollection.isEmpty()) {
                    _playlistsScreenState.postValue(
                        PlaylistsScreenState.Placeholder
                    )
                } else {

                    _playlistsScreenState.postValue(
                        PlaylistsScreenState.Content(
                            mapPlaylistCollectionToUIPlaylistCollection(playlistCollection)
                        )
                    )
                }
            }
        }
    }

    private fun mapPlaylistCollectionToUIPlaylistCollection(playlistCollection: List<Playlist>): List<PlaylistUIModel> {
        return playlistCollection.map { playlist -> playlistUIMapper.map(playlist) }
    }
}