package com.example.playlistmaker.settings.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val _themeSwitcherState = MutableLiveData(settingsInteractor.getThemeSettings())
    val themeSwitcherState : LiveData<ThemeSettings> = _themeSwitcherState

    fun setThemeSettings(isEnabled: Boolean) {
        settingsInteractor.updateThemeSetting(
            ThemeSettings(
                isDarkThemeEnabled = isEnabled
            )
        )
        _themeSwitcherState.value = ThemeSettings(
            isDarkThemeEnabled = isEnabled
        )
    }

    fun onShareButtonClick() {
        sharingInteractor.shareApp()
    }

    fun onSupportButtonClick() {
        sharingInteractor.openSupport()
    }

    fun onOfferButton() {
        sharingInteractor.openTerms()
    }
}