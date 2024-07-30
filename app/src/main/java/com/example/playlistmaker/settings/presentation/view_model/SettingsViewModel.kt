package com.example.playlistmaker.settings.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.settings.domain.model.ThemeSettings

class SettingsViewModel(
    // private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val _themeSwitcherState = MutableLiveData<ThemeSettings>(settingsInteractor.getThemeSettings())
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
}