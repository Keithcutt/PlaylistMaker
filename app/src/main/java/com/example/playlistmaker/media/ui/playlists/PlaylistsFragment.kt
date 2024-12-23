package com.example.playlistmaker.media.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.main.ui.activity.BindingFragment
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.presentation.state.PlaylistsScreenState
import com.example.playlistmaker.media.presentation.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : BindingFragment<FragmentPlaylistsBinding>() {

    private val viewModel: PlaylistsViewModel by viewModel()
    private val playlistsAdapter by lazy {
        PlaylistsAdapter()
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeUI()
        setupListeners()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshPlaylists()
    }

    private fun initializeUI() {
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = playlistsAdapter
    }

    private fun setupListeners() {
        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_newPlaylistFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.playlistsScreenState.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun render(state: PlaylistsScreenState) {
        when (state) {
            is PlaylistsScreenState.Placeholder -> showPlaceholder()
            is PlaylistsScreenState.Content -> showContent(state.playlists)
        }
    }

    private fun showPlaceholder() {
        binding.placeholderNotFound.isVisible = true
        binding.recyclerView.isVisible = false
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.placeholderNotFound.isVisible = false
        binding.recyclerView.isVisible = true
        playlistsAdapter.updatePlaylists(playlists)
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}