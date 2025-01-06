package com.example.playlistmaker.media.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.media.presentation.model.PlaylistUIModel
import com.example.playlistmaker.media.presentation.view_model.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.properties.Delegates

class EditPlaylistFragment : NewPlaylistFragment() {

    private var playlistId by Delegates.notNull<Int>()
    override val viewModel: EditPlaylistViewModel by viewModel { parametersOf(playlistId) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPlaylistId()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reconfigureUI()
        reconfigureBackNavigation()
        saveButtonReconfigure()
        observeViewModel()
    }

    private fun getPlaylistId() {
        playlistId = requireArguments().getInt(ARGS_PLAYLIST_ID)
    }

    private fun reconfigureUI() {
        binding.toolbar.title = getString(R.string.edit)
        binding.createButton.text = getString(R.string.save)
    }

    private fun reconfigureBackNavigation() {
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            }
        )
    }

    private fun saveButtonReconfigure() {
        binding.createButton.setOnClickListener {
            viewModel.updatePlaylist()
            findNavController().navigateUp()
        }
    }

    private fun observeViewModel() {
        viewModel.playlistDetails.observe(viewLifecycleOwner) { playlist ->
            bindData(playlist)
        }
    }

    private fun bindData(playlist: PlaylistUIModel) {
        binding.nameTextField.editText?.setText(playlist.playlistName)
        binding.descriptionTextField.editText?.setText(playlist.description)
        bindPlaylistCover(playlist.coverUri)
    }

    private fun bindPlaylistCover(uri: Uri?) {
        if (uri != null) {
            binding.playlistCover.foreground = null
            binding.playlistCover.background = null

            Glide.with(this@EditPlaylistFragment)
                .load(uri)
                .apply(
                    RequestOptions().transform(
                        MultiTransformation(
                            CenterCrop(),
                            RoundedCorners(dpToPx(PLAYLIST_COVER_CORNER_RADIUS, requireContext()))
                        )
                    )
                )
                .into(binding.playlistCover)
        }
    }

    companion object {
        private const val PLAYLIST_COVER_CORNER_RADIUS = 8f
        private const val ARGS_PLAYLIST_ID = "picked_playlist_id"
        fun createArgs(playlistId: Int): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)
    }
}