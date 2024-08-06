package com.example.playlistmaker.sharing.domain.navigator

interface ExternalNavigator {
    fun shareLink()
    fun openLink()
    fun openEmail()
}