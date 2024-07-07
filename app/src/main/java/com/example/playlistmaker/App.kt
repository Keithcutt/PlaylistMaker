package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator


class App : Application() {

    private companion object {
        // private const val PLAYLISTMAKER_PREFERENCES = "playlistmaker_preferences"
        private const val DARK_THEME = "key_for_theme_switcher"
    }

    private var darkTheme = false
    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreate() {
        super.onCreate()

        Creator.initApplication(this)

        sharedPreferences = Creator.provideSharedPreferences() // getSharedPreferences(PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE)
        switchTheme(
            sharedPreferences.getBoolean(
                DARK_THEME,
                resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)
        )

    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        sharedPreferences.edit()
            .putBoolean(DARK_THEME, darkTheme)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun isDarkThemeEnabled() = darkTheme
}