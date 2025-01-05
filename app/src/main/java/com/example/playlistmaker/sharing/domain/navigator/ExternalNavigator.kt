package com.example.playlistmaker.sharing.domain.navigator

import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track

interface ExternalNavigator {
    fun shareLink()
    fun openLink()
    fun openEmail()
    fun sharePlaylist(playlist: Playlist, tracksFromPlaylist: List<Track>)
}