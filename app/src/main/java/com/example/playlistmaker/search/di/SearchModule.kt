package com.example.playlistmaker.search.di

import android.content.Context
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.network.ITunesApiService
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.GetSearchTracksUseCase
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.impl.GetSearchTracksUseCaseImpl
import com.example.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel
import com.google.gson.Gson
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val PLAYLISTMAKER_PREFERENCES = "playlistmaker_preferences"

val searchModule = module {

    single {
        androidApplication().getSharedPreferences(
            PLAYLISTMAKER_PREFERENCES,
            Context.MODE_PRIVATE
        )
    }

    single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get(), get()) }
    single<TracksRepository> { TracksRepositoryImpl(get()) }

    single<ITunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApiService::class.java)
    }

    single { Gson() }
    single<NetworkClient> { RetrofitNetworkClient(get(), get()) }


    factory<SearchHistoryInteractor> { SearchHistoryInteractorImpl(get()) }
    factory<GetSearchTracksUseCase> { GetSearchTracksUseCaseImpl(get()) }

    viewModel {
        SearchViewModel(get(), get())
    }
}

