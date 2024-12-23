package com.example.playlistmaker.media.domain.model

data class Playlist(
    val playlistId: Int,
    val playlistName: String,
    val description: String?,
    val coverUri: String?,
    var trackIdsList: List<Int>,
    var trackCount: Int
)