package com.example.playlistmaker.media.ui.playlist_collection_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistCollectionBinding
import com.example.playlistmaker.main.ui.activity.BindingFragment
import com.example.playlistmaker.media.presentation.model.PlaylistUIModel
import com.example.playlistmaker.media.presentation.state.PlaylistCollectionScreenState
import com.example.playlistmaker.media.presentation.view_model.PlaylistCollectionViewModel
import com.example.playlistmaker.media.ui.playlist_screen.PlaylistFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistCollectionFragment : BindingFragment<FragmentPlaylistCollectionBinding>() {

    private var isClickAllowed = true
    private val viewModel: PlaylistCollectionViewModel by viewModel()
    private val playlistCollectionAdapter by lazy {
        PlaylistCollectionAdapter {
            openPlaylistDetails(it)
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistCollectionBinding {
        return FragmentPlaylistCollectionBinding.inflate(inflater, container, false)
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
        binding.recyclerView.adapter = playlistCollectionAdapter
    }

    private fun setupListeners() {
        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_newPlaylistFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.playlistCollectionScreenState.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun render(state: PlaylistCollectionScreenState) {
        when (state) {
            is PlaylistCollectionScreenState.Placeholder -> showPlaceholder()
            is PlaylistCollectionScreenState.Content -> showContent(state.playlists)
        }
    }

    private fun showPlaceholder() {
        binding.placeholderNotFound.isVisible = true
        binding.recyclerView.isVisible = false
    }

    private fun showContent(playlists: List<PlaylistUIModel>) {
        binding.placeholderNotFound.isVisible = false
        binding.recyclerView.isVisible = true
        playlistCollectionAdapter.updatePlaylists(playlists)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun openPlaylistDetails(playlistId: Int) {
        if (clickDebounce()) {
            findNavController().navigate(
                R.id.action_mediaFragment_to_playlistFragment,
                PlaylistFragment.createArgs(playlistId)
            )
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 500L
        fun newInstance() = PlaylistCollectionFragment()
    }
}