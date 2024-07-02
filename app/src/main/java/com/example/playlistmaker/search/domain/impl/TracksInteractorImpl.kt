package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.Resource
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    // Здесь можно было бы пересортировать список треков, отфильтровать, убрав ненужные результаты поиска
    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            when(val resource = repository.searchTracks(expression)) {
                is Resource.Success -> { consumer.consume(resource.value, null) }
                is Resource.Error -> { consumer.consume(null, resource.message) }
            }
        }
    }
}