package com.example.playlistmaker.media.di

import androidx.room.Room
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.impl.FavouritesRepositoryImpl
import com.example.playlistmaker.media.domain.db.FavouritesInteractor
import com.example.playlistmaker.media.domain.db.FavouritesRepository
import com.example.playlistmaker.media.domain.impl.FavouritesInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataBaseModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }

    single<FavouritesRepository> {
        FavouritesRepositoryImpl(get())
    }

    single<FavouritesInteractor> {
        FavouritesInteractorImpl(get())
    }
}