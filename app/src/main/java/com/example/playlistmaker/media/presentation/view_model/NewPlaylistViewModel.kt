package com.example.playlistmaker.media.presentation.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db_api.PlaylistsInteractor
import com.example.playlistmaker.media.domain.local_storage_api.LocalStorageInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.launch

open class NewPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val localStorageInteractor: LocalStorageInteractor
) : ViewModel() {

    private val _isAnythingProvided = MutableLiveData(false)
    val isAnythingProvided: LiveData<Boolean> = _isAnythingProvided

    protected var playlistName: String = ""
    protected var playlistDescription: String? = null
    protected var playlistCoverFileName: String? = null


    fun createPlaylist() {
        viewModelScope.launch {
            playlistsInteractor.insertPlaylist(
                Playlist(
                    0,
                    playlistName,
                    playlistDescription,
                    playlistCoverFileName,
                    emptyList(),
                    0
                )
            )
        }
    }

    fun setPlaylistCover(uri: Uri) {
        val coverUriString = uri.toString()
        playlistCoverFileName = localStorageInteractor.savePlaylistCover(coverUriString)
        _isAnythingProvided.postValue(true)
    }

    fun updatePlaylistName(name: String) {
        if (name.isNotBlank()) {
            _isAnythingProvided.postValue(true)
        } else if (playlistDescription == null && playlistCoverFileName == null && name.isBlank()) {
            _isAnythingProvided.postValue(false)
        }

        playlistName = name
    }

    fun updatePlaylistDescription(description: String?) {
        if (description == null && playlistName == "" && playlistCoverFileName == null) {
            _isAnythingProvided.postValue(false)
        } else if (description?.isNotBlank() == true) {
            _isAnythingProvided.postValue(true)
        }

        playlistDescription = description
    }
}