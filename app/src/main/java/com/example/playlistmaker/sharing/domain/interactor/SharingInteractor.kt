package com.example.playlistmaker.sharing.domain.interactor

import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track

interface SharingInteractor {
    fun shareApp()
    fun openTerms()
    fun openSupport()
    fun sharePlaylist(playlist: Playlist, tracksFromPlaylist: List<Track>)
}