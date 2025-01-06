package com.example.playlistmaker.media.ui.playlist_screen

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.main.ui.activity.BindingFragment
import com.example.playlistmaker.media.presentation.model.PlaylistUIModel
import com.example.playlistmaker.media.presentation.state.PlaylistScreenState
import com.example.playlistmaker.media.presentation.view_model.PlaylistViewModel
import com.example.playlistmaker.media.ui.EditPlaylistFragment
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TracksAdapter
import com.example.playlistmaker.util.state.BottomSheetState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.properties.Delegates

class PlaylistFragment : BindingFragment<FragmentPlaylistBinding>() {

    private var playlistId by Delegates.notNull<Int>()
    private val viewmodel: PlaylistViewModel by viewModel { parametersOf(playlistId) }
    private var isClickAllowed = true
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val tracksFromPlaylistAdapter by lazy {
        TracksAdapter({
            startPlayerActivity(it)
        }, {
            removeTrackFromPlaylist(it)
        })
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistBinding {
        return FragmentPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onResume() {
        super.onResume()
        viewmodel.refreshPlaylistDetails(playlistId)
        viewmodel.getTracksFromPlaylist(playlistId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getPlaylistId()
        setupRecyclerView()
        setupListeners()
        setupMenuBottomSheet()
        observeViewModel()
    }

    private fun getPlaylistId() {
        playlistId = requireArguments().getInt(ARGS_PLAYLIST_ID)
    }

    private fun bindData(playlistDetails: PlaylistUIModel) {
        binding.playlistName.text = playlistDetails.playlistName
        binding.playlistDescription.text = playlistDetails.description
        binding.trackCount.text = requireContext().resources.getQuantityString(
            R.plurals.playlist_track_count,
            playlistDetails.trackCount,
            playlistDetails.trackCount
        )

        Glide.with(this@PlaylistFragment)
            .load(playlistDetails.coverUri)
            .centerCrop()
            .placeholder(R.drawable.placeholder_237x234)
            .into(binding.cover)
    }

    private fun bindPlaylistDuration(durationMins: Int) {
        binding.playlistDuration.text = requireContext().resources.getQuantityString(
            R.plurals.playlist_duration,
            durationMins,
            durationMins
        )
    }

    private fun bindBottomSheetMenuData(playlistDetails: PlaylistUIModel) {
        binding.bottomSheetPlaylistName.text = playlistDetails.playlistName
        binding.bottomSheetTrackCount.text = requireContext().resources.getQuantityString(
            R.plurals.playlist_track_count,
            playlistDetails.trackCount,
            playlistDetails.trackCount
        )

        Glide.with(this@PlaylistFragment)
            .load(playlistDetails.coverUri)
            .centerCrop()
            .placeholder(R.drawable.ic_placeholder_35x35)
            .into(binding.bottomSheetPlaylistCover)
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.overlay.setOnClickListener {
            updateMenuBottomSheetState(BottomSheetState.HIDDEN)
        }

        binding.menuButton.setOnClickListener {
            updateMenuBottomSheetState(BottomSheetState.COLLAPSED)
        }

        binding.shareButton.setOnClickListener {
            viewmodel.sharePlaylist(playlistId)
        }

        binding.bottomSheetShareButton.setOnClickListener {
            updateMenuBottomSheetState(BottomSheetState.HIDDEN)
            viewmodel.sharePlaylist(playlistId)
        }

        binding.bottomSheetDeleteButton.setOnClickListener {
            deletePlaylist(playlistId)
        }

        binding.bottomSheetEditButton.setOnClickListener {
            openEditScreen()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = tracksFromPlaylistAdapter
    }

    private fun observeViewModel() {
        viewmodel.playlistDetails.observe(viewLifecycleOwner) { playlistDetails ->
            bindData(playlistDetails)
            bindBottomSheetMenuData(playlistDetails)
        }

        viewmodel.playlistDuration.observe(viewLifecycleOwner) { playlistDurationMins ->
            bindPlaylistDuration(playlistDurationMins)
        }

        viewmodel.playlistScreenState.observe(viewLifecycleOwner) { state ->
            render(state)
        }

        viewmodel.noTracksToShare.observe(viewLifecycleOwner) { showToast() }
    }

    private fun showToast() {
        Toast.makeText(
            requireContext(),
            getString(R.string.no_tracks_to_share_message),
            Toast.LENGTH_SHORT
        ).show()
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

    private fun openEditScreen() {
        findNavController().navigate(
            R.id.action_playlistFragment_to_editPlaylistFragment,
            EditPlaylistFragment.createArgs(playlistId)
        )
    }

    private fun deletePlaylist(playlistId: Int) {
        MaterialAlertDialogBuilder(requireContext(), R.style.RemovingAlertDialog)
            .setTitle(getString(R.string.delete_playlist_dialog_message).format(binding.playlistName.text))
            .setNegativeButton(R.string.no_upper_case) { _, _ ->

            }
            .setPositiveButton(R.string.yes_upper_case) { _, _ ->
                viewmodel.deletePlaylist(playlistId)
                findNavController().navigateUp()
            }.show()

    }

    private fun removeTrackFromPlaylist(track: Track) {
        MaterialAlertDialogBuilder(requireContext(), R.style.RemovingAlertDialog)
            .setTitle(R.string.remove_track_dialog_alert_message)
            .setNegativeButton(R.string.no_upper_case) { _, _ ->

            }
            .setPositiveButton(R.string.yes_upper_case) { _, _ ->
                viewmodel.removeTrackFromPlaylist(track)
            }.show()
    }

    private fun setupMenuBottomSheet() {
        menuBottomSheetBehavior =
            BottomSheetBehavior.from(binding.menuBottomSheetContainer).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }

        menuBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = (slideOffset + 1f) / (2f - (slideOffset + 1f))
            }
        })
    }

    private fun updateMenuBottomSheetState(state: BottomSheetState) {
        when (state) {
            BottomSheetState.HIDDEN -> menuBottomSheetBehavior.state =
                BottomSheetBehavior.STATE_HIDDEN

            BottomSheetState.COLLAPSED -> menuBottomSheetBehavior.state =
                BottomSheetBehavior.STATE_COLLAPSED

            BottomSheetState.EXPANDED -> menuBottomSheetBehavior.state =
                BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun render(state: PlaylistScreenState) {
        when (state) {
            is PlaylistScreenState.PlaylistWithTracks -> {
                showTracks(state.tracksFromPlaylist)
            }

            else -> {
                hideTracks()
            }
        }
    }

    private fun showTracks(tracksFromPlaylist: List<Track>) {
        binding.recyclerView.adapter = tracksFromPlaylistAdapter
        binding.tracksBottomSheetContainer.isVisible = true
        tracksFromPlaylistAdapter.setTracks(tracksFromPlaylist)
    }

    private fun hideTracks() {
        binding.tracksBottomSheetContainer.isVisible = false
        Toast.makeText(requireContext(), getString(R.string.no_tracks_message), Toast.LENGTH_SHORT)
            .show()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 500L
        private const val TRACK_KEY = "track"
        private const val ARGS_PLAYLIST_ID = "picked_playlist_id"

        fun createArgs(playlistId: Int): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)
    }
}