package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.PlayerState

fun interface OnPlayerStateChangeListener {
    fun onChange(state: PlayerState)
}