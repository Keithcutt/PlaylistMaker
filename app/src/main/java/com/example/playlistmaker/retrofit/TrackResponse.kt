package com.example.playlistmaker.retrofit

import com.example.playlistmaker.Track

data class TrackResponse(val resultCount: Int,
                         val results: List<Track> )
