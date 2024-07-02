package com.example.playlistmaker.search.domain.models

data class Track (
    val trackName: String,
    val artistName: String,
    val trackTime: Long, // В строку, но не здесь
    val artworkUrl100: String,
    val trackId: Int,
    val collectionName: String,
    val releaseDate: String, // Обрезать по символу "-"
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) {
    // Поправить в плеере, чтобы использовался объект класса Track
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}