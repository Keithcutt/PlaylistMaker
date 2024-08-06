package com.example.playlistmaker.settings.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator

class SettingsViewModelFactory : ViewModelProvider.Factory {

    private val sp = Creator.provideSharedPreferences()
    private val settingsInteractor = Creator.provideSettingsInteractor(sp)
    private val sharingInteractor = Creator.provideSharingInteractor()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(
                settingsInteractor,
                sharingInteractor
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}