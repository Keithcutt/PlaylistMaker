package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor
import com.example.playlistmaker.sharing.domain.navigator.ExternalNavigator

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator
) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.shareLink()
    }

    override fun openTerms() {
        externalNavigator.openLink()
    }

    override fun openSupport() {
        externalNavigator.openEmail()
    }

    override fun sharePlaylist(playlist: Playlist, tracksFromPlaylist: List<Track>) {
        externalNavigator.sharePlaylist(playlist, tracksFromPlaylist)
    }

}