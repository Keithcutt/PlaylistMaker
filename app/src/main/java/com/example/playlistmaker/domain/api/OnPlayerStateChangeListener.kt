package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.PlayerState

interface OnPlayerStateChangeListener {
    fun onChange(state: PlayerState)
}