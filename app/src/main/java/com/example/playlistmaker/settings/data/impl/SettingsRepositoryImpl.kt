package com.example.playlistmaker.settings.data.impl

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.data.repository.SettingsRepository
import com.example.playlistmaker.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(private val sharedPrefs: SharedPreferences) : SettingsRepository {
    private companion object {
        private const val DARK_THEME = "key_for_theme_switcher"
    }

    private var darkTheme = ThemeSettings(
        sharedPrefs.getBoolean(DARK_THEME, false)
    )


    override fun getThemeSettings(): ThemeSettings {
        return darkTheme
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        darkTheme = settings

        sharedPrefs.edit()
            .putBoolean(DARK_THEME, darkTheme.isDarkThemeEnabled)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (settings.isDarkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}