package com.example.playlistmaker.settings.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor

class SettingsViewModelFactory : ViewModelProvider.Factory {

    private val sp = Creator.provideSharedPreferences()
    private val settingsInteractor = Creator.provideSettingsInteractor(sp)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(
                // Не забудь про другой интерактор
                settingsInteractor
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}