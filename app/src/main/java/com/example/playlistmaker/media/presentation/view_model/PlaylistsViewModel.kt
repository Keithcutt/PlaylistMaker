package com.example.playlistmaker.media.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistsInteractor
import com.example.playlistmaker.media.presentation.state.PlaylistsScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistsInteractor: PlaylistsInteractor) : ViewModel() {

    private val _playlistsScreenState = MutableLiveData<PlaylistsScreenState>()
    val playlistsScreenState = _playlistsScreenState

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
                        PlaylistsScreenState.Content(playlistCollection)
                    )
                }
            }
        }
    }
}