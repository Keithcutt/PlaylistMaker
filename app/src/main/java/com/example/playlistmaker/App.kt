package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.player.di.playerModule
import com.example.playlistmaker.search.di.searchModule
import com.example.playlistmaker.settings.di.settingsModule
import com.example.playlistmaker.sharing.di.sharingModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin


class App : Application() {

    companion object {
        private const val DARK_THEME = "key_for_theme_switcher"
        private const val PLAYLISTMAKER_PREFERENCES = "playlistmaker_preferences"

    }

    private var darkTheme = false
    private lateinit var sharedPreferences : SharedPreferences


    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(playerModule, searchModule, settingsModule, sharingModule)
        }

        sharedPreferences = getSharedPreferences(PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE)
        switchTheme(
            sharedPreferences.getBoolean(
                DARK_THEME,
                resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)
        )
    }

    private fun switchTheme(darkThemeEnabled: Boolean) {
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
}