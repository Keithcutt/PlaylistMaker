package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.Resource
import com.example.playlistmaker.search.domain.api.GetSearchTracksUseCase
import com.example.playlistmaker.search.domain.api.TracksRepository
import java.util.concurrent.Executors

class GetSearchTracksUseCaseImpl(private val repository: TracksRepository) : GetSearchTracksUseCase {

    private val executor = Executors.newCachedThreadPool()

    // Здесь можно было бы пересортировать список треков, отфильтровать, убрав ненужные результаты поиска
    override fun execute(expression: String, consumer: GetSearchTracksUseCase.TracksConsumer) {
        executor.execute {
            when(val resource = repository.searchTracks(expression)) {
                is Resource.Success -> { consumer.consume(resource.value, null) }
                is Resource.Error -> { consumer.consume(null, resource.message) }
            }
        }
    }
}