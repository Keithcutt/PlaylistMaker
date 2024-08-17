package com.example.playlistmaker.sharing.di

import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor
import com.example.playlistmaker.sharing.domain.navigator.ExternalNavigator
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val sharingModule = module {

    single<ExternalNavigator> { ExternalNavigatorImpl(androidApplication()) }
    single<SharingInteractor> { SharingInteractorImpl(get()) }
}