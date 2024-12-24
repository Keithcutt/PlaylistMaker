package com.example.playlistmaker.media.presentation.model

import android.net.Uri

data class PlaylistUIModel(
    val playlistId: Int,
    val playlistName: String,
    val description: String?,
    val coverUri: Uri?,
    var trackIdsList: List<Int>,
    var trackCount: Int
)