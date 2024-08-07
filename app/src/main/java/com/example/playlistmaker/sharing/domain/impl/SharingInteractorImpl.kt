package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.domain.navigator.ExternalNavigator
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor

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

}