package com.example.playlistmaker.player.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.search.domain.models.Track
class PlayerViewModelFactory(private val currentTrack: Track) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerViewModel(currentTrack) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}