package com.example.playlistmaker.sharing.domain.model

data class EmailData (
    val recipient: ArrayList<String>,
    val subject: String,
    val text: String
)