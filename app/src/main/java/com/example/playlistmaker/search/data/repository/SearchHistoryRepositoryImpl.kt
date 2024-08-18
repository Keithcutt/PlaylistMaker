package com.example.playlistmaker.search.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson

class SearchHistoryRepositoryImpl(private val sharedPreferences: SharedPreferences, private val gson: Gson) : SearchHistoryRepository {
    companion object {
        const val SEARCHED_TRACKS_KEY = "searched_tracks"
        const val EMPTY_STRING = ""
        const val SEARCHED_TRACKS_CAPACITY = 10
    }

    private var searchedTracks: MutableList<Track> = mutableListOf()
    private val json = sharedPreferences.getString(SEARCHED_TRACKS_KEY, null)

    init {
        if (!json.isNullOrEmpty()) {
            val array: Array<Track> = createTracksFromJson(json)
            array.forEach {
                searchedTracks.add(it)
            }
        }
    }

    override fun save(track: Track) {
        for (i in searchedTracks) {
            if (track.trackId == i.trackId) {
                searchedTracks.remove(i)
                break
            }
        }
        searchedTracks.add(0, track)
        if (searchedTracks.size > SEARCHED_TRACKS_CAPACITY) {
            searchedTracks.removeAt(SEARCHED_TRACKS_CAPACITY)
        }

        val updatedJson = createJsonFromTracks(searchedTracks)
        sharedPreferences.edit()
            .putString(SEARCHED_TRACKS_KEY, updatedJson)
            .apply()
    }

    override fun clear() {
        searchedTracks.clear()
        sharedPreferences.edit()
            .putString(SEARCHED_TRACKS_KEY, EMPTY_STRING)
            .apply()
    }

    override fun getSearchHistory(): MutableList<Track> = searchedTracks

    override fun isSearchHistoryNotEmpty(): Boolean = searchedTracks.isNotEmpty()
    private fun createJsonFromTracks(tracks: MutableList<Track>): String {
        return gson.toJson(tracks)
    }

    private fun createTracksFromJson(json: String): Array<Track> {
        return gson.fromJson(json, Array<Track>::class.java)
    }
}