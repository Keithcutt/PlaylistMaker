package com.example.playlistmaker.player.domain.repository

import com.example.playlistmaker.player.domain.models.PlayerState

fun interface OnPlayerStateChangeListener {
    fun onChange(state: PlayerState)
}