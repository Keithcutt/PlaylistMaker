package com.example.playlistmaker.player.ui

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.domain.state.PlayerState
import com.example.playlistmaker.player.presentation.view_model.PlayerViewModel
import com.example.playlistmaker.player.presentation.view_model.PlayerViewModelFactory
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private companion object {
        const val ARTWORK_CORNER_RADIUS = 8
    }

    private lateinit var binding: ActivityPlayerBinding
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private lateinit var currentTrack: Track

    private val viewModel : PlayerViewModel by lazy {
        ViewModelProvider(this, PlayerViewModelFactory(currentTrack))[PlayerViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentTrack = Creator.provideCurrentTrack(intent)
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
    }

    private fun observeViewModel() {
        viewModel.playbackState.observe(this) { state ->
            render(state)
        }

        viewModel.playbackProgress.observe(this) { progressMillis ->
            playbackProgressCounter(progressMillis)
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
}