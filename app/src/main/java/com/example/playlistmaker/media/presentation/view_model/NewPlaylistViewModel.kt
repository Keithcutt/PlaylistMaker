package com.example.playlistmaker.media.presentation.view_model

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistsInteractor
import com.example.playlistmaker.media.domain.local_storage_api.SavePlaylistCoverUseCase
import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val savePlaylistCoverUseCase: SavePlaylistCoverUseCase
) : ViewModel() {

    private val _isAnythingProvided = MutableLiveData<Boolean>(false)
    val isAnythingProvided = _isAnythingProvided

    private var playlistName: String = ""
    private var playlistDescription: String? = null
    private var playlistCover: String? = null


    fun createPlaylist() {
        viewModelScope.launch {
            playlistsInteractor.insertPlaylist(
                Playlist(0, playlistName, playlistDescription, playlistCover, emptyList(), 0)
            )
        }

    }

    fun setPlaylistCover(uri: Uri) {
        val coverUriString = uri.toString()
        playlistCover = coverUriString
        savePlaylistCoverUseCase.execute(coverUriString)
        _isAnythingProvided.postValue(true)
    }

    fun setPlaylistName(name: String) {
        if (name.isNotBlank()) {
            _isAnythingProvided.postValue(true)
        } else if (playlistDescription == null && playlistCover == null && name.isBlank()) {
            _isAnythingProvided.postValue(false)
        }

        playlistName = name
    }

    fun setPlaylistDescription(description: String?) {
        if (description == null && playlistName == "" && playlistCover == null) {
            _isAnythingProvided.postValue(false)
        } else if (description?.isNotBlank() == true) {
            _isAnythingProvided.postValue(true)
        }

        playlistDescription = description
    }
}