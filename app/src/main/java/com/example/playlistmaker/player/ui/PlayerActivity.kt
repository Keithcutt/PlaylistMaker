package com.example.playlistmaker.player.ui

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.domain.state.PlayerState
import com.example.playlistmaker.player.presentation.mapper.TrackMapper
import com.example.playlistmaker.player.presentation.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private companion object {
        const val ARTWORK_CORNER_RADIUS = 8
    }

    private lateinit var binding: ActivityPlayerBinding
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private lateinit var currentTrack: Track
    private val trackMapper: TrackMapper by inject()

    private val viewModel: PlayerViewModel by viewModel { parametersOf(currentTrack) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentTrack = trackMapper.getFromIntent(intent)
        bindData(currentTrack)

        setupListeners()
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
            .transform(RoundedCorners(dpToPx(ARTWORK_CORNER_RADIUS.toFloat(), this@PlayerActivity)))
            .placeholder(R.drawable.placeholder_237x234)
            .into(binding.trackCover)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener { finish() }

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.addToFavouriteButton.setOnClickListener {
            viewModel.onFavouriteClicked()
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