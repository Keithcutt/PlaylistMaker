package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.PlayerState

interface OnPlayerStateChangeListener {
    fun onChange(state: PlayerState)
}