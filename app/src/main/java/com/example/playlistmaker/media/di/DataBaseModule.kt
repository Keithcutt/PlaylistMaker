package com.example.playlistmaker.media.di

import androidx.room.Room
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.impl.FavouritesRepositoryImpl
import com.example.playlistmaker.media.data.impl.PlaylistRepositoryImpl
import com.example.playlistmaker.media.data.mapper.PlaylistEntityMapper
import com.example.playlistmaker.media.domain.db_api.FavouritesInteractor
import com.example.playlistmaker.media.domain.db_api.FavouritesRepository
import com.example.playlistmaker.media.domain.db_api.PlaylistsInteractor
import com.example.playlistmaker.media.domain.db_api.PlaylistsRepository
import com.example.playlistmaker.media.domain.impl.FavouritesInteractorImpl
import com.example.playlistmaker.media.domain.impl.PlaylistsInteractorImpl
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataBaseModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    single<FavouritesRepository> {
        FavouritesRepositoryImpl(get())
    }

    single<FavouritesInteractor> {
        FavouritesInteractorImpl(get())
    }


    single { Gson() }
    single<PlaylistEntityMapper> { PlaylistEntityMapper(get()) }
    single<PlaylistsRepository> {
        PlaylistRepositoryImpl(get(), get())
    }
    single<PlaylistsInteractor> {
        PlaylistsInteractorImpl(get())
    }
}