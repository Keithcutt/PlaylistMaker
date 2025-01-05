package com.example.playlistmaker.player.ui

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.media.presentation.model.PlaylistUIModel
import com.example.playlistmaker.media.ui.NewPlaylistFragment
import com.example.playlistmaker.player.domain.state.PlayerState
import com.example.playlistmaker.player.presentation.mapper.TrackMapper
import com.example.playlistmaker.player.presentation.state.AddingTrackToPlaylistStatus
import com.example.playlistmaker.player.presentation.view_model.PlayerViewModel
import com.example.playlistmaker.util.state.BottomSheetState
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private companion object {
        const val ARTWORK_CORNER_RADIUS = 8f
    }

    private lateinit var binding: ActivityPlayerBinding
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private lateinit var currentTrack: Track
    private val trackMapper: TrackMapper by inject()

    private val viewModel: PlayerViewModel by viewModel { parametersOf(currentTrack) }

    private val playlistsAdapter: BottomSheetPlaylistsAdapter by lazy {
        BottomSheetPlaylistsAdapter {
            onPlaylistClickListener(it)
        }
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentTrack = trackMapper.getFromIntent(intent)
        bindData(currentTrack)

        setupListeners()
        setupBottomSheet()
        initializeRecycler()
        observeViewModel()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    private fun bindData(model: Track) {
        binding.playbackProgress.text = getString(R.string.zeroZero)
        binding.trackName.text = model.trackName
        binding.artistName.text = model.artistName
        binding.genreValue.text = model.primaryGenreName
        binding.countryValue.text = model.country
        binding.yearValue.text = model.releaseDate
        binding.albumValue.text = model.collectionName
        binding.durationValue.text = dateFormat.format(model.trackTime)

        Glide.with(this@PlayerActivity)
            .load(model.getCoverArtwork())
            .centerCrop()
            .transform(RoundedCorners(dpToPx(ARTWORK_CORNER_RADIUS, this@PlayerActivity)))
            .placeholder(R.drawable.placeholder_237x234)
            .into(binding.trackCover)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    private fun initializeRecycler() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = playlistsAdapter
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener { finish() }

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.addToFavouriteButton.setOnClickListener {
            viewModel.onFavouriteClicked()
        }

        binding.addToPlaylistButton.setOnClickListener {
            viewModel.refreshPlaylistCollection()
            updateBottomSheetState(BottomSheetState.COLLAPSED)
        }

        binding.overlay.setOnClickListener {
            updateBottomSheetState(BottomSheetState.HIDDEN)
        }

        binding.newPlaylistButton.setOnClickListener {
            openNewPlaylistFragment()
        }
    }

    private fun observeViewModel() {
        viewModel.playbackState.observe(this) { state ->
            render(state)
        }

        viewModel.playbackProgress.observe(this) { progressMillis ->
            playbackProgressCounter(progressMillis)
        }

        viewModel.favouriteStatus.observe(this) { status ->
            favouriteButtonState(status)
        }

        viewModel.playlistCollection.observe(this) { playlistCollection ->
            showPlaylists(playlistCollection)
        }

        viewModel.trackAddedStatus.observe(this) { status ->
            makeToast(status)
        }
    }

    private fun makeToast(status: AddingTrackToPlaylistStatus) {
        when (status) {
            is AddingTrackToPlaylistStatus.TrackAdded -> Toast.makeText(
                this@PlayerActivity,
                getString(R.string.track_added_to_playlist).format(status.playlistName),
                Toast.LENGTH_SHORT
            ).show()

            is AddingTrackToPlaylistStatus.TrackAlreadyIn -> Toast.makeText(
                this@PlayerActivity,
                getString(R.string.track_is_already_in).format(status.playlistName),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun onPlaylistClickListener(playlist: PlaylistUIModel) {
        viewModel.addTrackToPlaylist(currentTrack, playlist)
        updateBottomSheetState(BottomSheetState.HIDDEN)
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior =
            BottomSheetBehavior.from(binding.bottomSheetContainer).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }

        bottomSheetBehavior.addBottomSheetCallback(object :
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

    private fun updateBottomSheetState(state: BottomSheetState) {
        when (state) {
            BottomSheetState.HIDDEN -> bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            BottomSheetState.COLLAPSED -> bottomSheetBehavior.state =
                BottomSheetBehavior.STATE_COLLAPSED

            BottomSheetState.EXPANDED -> bottomSheetBehavior.state =
                BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun setFragmentContainerVisibility(showFragment: Boolean) {
        binding.playerContainer.visibility = if (showFragment) View.GONE else View.VISIBLE
        binding.fragmentContainer.visibility = if (showFragment) View.VISIBLE else View.GONE
    }

    private fun showPlaylists(playlists: List<PlaylistUIModel>) {
        playlistsAdapter.updatePlaylists(playlists)
    }

    private fun openNewPlaylistFragment() {
        updateBottomSheetState(BottomSheetState.HIDDEN)
        setFragmentContainerVisibility(true)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, NewPlaylistFragment.newInstance(true))
            .addToBackStack(null)
            .commit()

        supportFragmentManager.setFragmentResultListener(
            NewPlaylistFragment.RESULT_KEY, this
        ) { _, _ ->
            updateBottomSheetState(BottomSheetState.COLLAPSED)
            setFragmentContainerVisibility(false)
            supportFragmentManager.popBackStack()
            viewModel.refreshPlaylistCollection()
        }

    }


    private fun render(state: PlayerState) {
        when (state) {
            PlayerState.PLAYING -> onPlaying()
            PlayerState.PAUSED -> onPaused()
            PlayerState.PREPARED -> onPrepared()
            PlayerState.DEFAULT -> onDefault()
        }
    }

    private fun onPlaying() {
        binding.playButton.setImageResource(R.drawable.btn_pause)
    }

    private fun onPaused() {
        binding.playButton.setImageResource(R.drawable.btn_play)
    }

    private fun onPrepared() {
        binding.playbackProgress.text = getString(R.string.zeroZero)
        binding.playButton.setImageResource(R.drawable.btn_play)
    }

    private fun onDefault() {
        binding.playButton.isEnabled = true
    }

    private fun playbackProgressCounter(progressMillis: Int) {
        binding.playbackProgress.text = dateFormat.format(progressMillis)
    }


    private fun favouriteButtonState(isFavourite: Boolean) {
        when (isFavourite) {
            true -> binding.addToFavouriteButton.setImageResource(R.drawable.btn_like_active)
            else -> binding.addToFavouriteButton.setImageResource(R.drawable.btn_like_inactive)
        }
    }
}