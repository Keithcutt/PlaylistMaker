package com.example.playlistmaker.media.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.databinding.FragmentFavouritesBinding
import com.example.playlistmaker.main.ui.activity.BindingFragment
import com.example.playlistmaker.media.presentation.state.FavouritesScreenState
import com.example.playlistmaker.media.presentation.view_model.FavouritesViewModel
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TracksAdapter
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouritesFragment : BindingFragment<FragmentFavouritesBinding>() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val TRACK_KEY = "track"

        fun newInstance() = FavouritesFragment()
    }

    private val viewModel: FavouritesViewModel by viewModel()
    private var isClickAllowed = true

    private val tracksAdapter: TracksAdapter by lazy {
        TracksAdapter ({
            startPlayerActivity(it)
        })
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavouritesBinding {
        return FragmentFavouritesBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.refreshFavourites()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshFavourites()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun startPlayerActivity(track: Track) {
        if (clickDebounce()) {
            val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
            playerIntent.putExtra(TRACK_KEY, Gson().toJson(track))
            startActivity(playerIntent)
        }
    }

    private fun observeViewModel() {
        viewModel.favouritesScreenState.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun render(state: FavouritesScreenState) {
        when (state) {
            is FavouritesScreenState.EmptyScreen -> showPlaceholder()
            is FavouritesScreenState.Favourites -> showContent(state.favouriteTracks)
        }
    }

    private fun showPlaceholder() {
        binding.placeholder.isVisible = true
        binding.recyclerView.isVisible = false
    }

    private fun showContent(favouriteTracks: List<Track>) {
        binding.placeholder.isVisible = false
        binding.recyclerView.isVisible = true
        tracksAdapter.setTracks(favouriteTracks)
        binding.recyclerView.adapter = tracksAdapter
    }
}