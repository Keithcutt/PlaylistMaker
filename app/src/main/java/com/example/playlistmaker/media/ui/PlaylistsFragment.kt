package com.example.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.main.ui.activity.BindingFragment
import com.example.playlistmaker.media.presentation.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : BindingFragment<FragmentPlaylistsBinding>() {

    private val viewModel: PlaylistsViewModel by viewModel()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}