package com.example.playlistmaker.settings.di

import android.content.Context
import com.example.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.settings.domain.repository.SettingsRepository
import com.example.playlistmaker.settings.presentation.view_model.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private const val PLAYLISTMAKER_PREFERENCES = "playlistmaker_preferences"

val settingsModule = module {

    single {
        androidContext()
            .getSharedPreferences(PLAYLISTMAKER_PREFERENCES, Context.MODE_PRIVATE)
    }

    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<SettingsInteractor> { SettingsInteractorImpl(get()) }

    viewModel{
        SettingsViewModel(get(), get())
    }
}