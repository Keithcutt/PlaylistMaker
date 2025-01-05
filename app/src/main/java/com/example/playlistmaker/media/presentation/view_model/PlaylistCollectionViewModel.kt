package com.example.playlistmaker.media.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db_api.PlaylistsInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.presentation.mapper.PlaylistUIMapper
import com.example.playlistmaker.media.presentation.model.PlaylistUIModel
import com.example.playlistmaker.media.presentation.state.PlaylistCollectionScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistCollectionViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val playlistUIMapper: PlaylistUIMapper
) : ViewModel() {

    private val _playlistCollectionScreenState = MutableLiveData<PlaylistCollectionScreenState>()
    val playlistCollectionScreenState: LiveData<PlaylistCollectionScreenState> = _playlistCollectionScreenState

    init {
        refreshPlaylists()
    }

    fun refreshPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.getPlaylists().collect { playlistCollection ->

                if (playlistCollection.isEmpty()) {
                    _playlistCollectionScreenState.postValue(
                        PlaylistCollectionScreenState.Placeholder
                    )
                } else {

                    _playlistCollectionScreenState.postValue(
                        PlaylistCollectionScreenState.Content(
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