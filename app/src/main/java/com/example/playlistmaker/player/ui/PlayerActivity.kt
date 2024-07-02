package com.example.playlistmaker.player.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private companion object {
        const val ARTWORK_CORNER_RADIUS = 8
    }

    private lateinit var binding: ActivityPlayerBinding
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private val handler = Handler(Looper.getMainLooper())
    private val playbackRunnable = { playbackProgressCounter(playerInteractor.getPlayerState()) }

    private val playerInteractor = Creator.providePlayerInteractor()
    private lateinit var currentTrack: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentTrack = Creator.provideCurrentTrack(intent)
        bindData(currentTrack)

        binding.backButton.setOnClickListener { finish() }

        playerInteractor.setOnPlayerStateChangeListener { state -> playbackCases(state) }
        playerInteractor.preparePlayer(currentTrack.previewUrl)
        binding.playButton.setOnClickListener {
            playbackControl(playerInteractor.getPlayerState())
        }
    }

    override fun onPause() {
        super.onPause()
        playerInteractor.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.releasePlayer()

        handler.removeCallbacks(playbackRunnable)
    }

    private fun bindData(model: Track) {
        binding.playbackProgress.text = getString(R.string.zeroZero)
        binding.trackName.text = model.trackName
        binding.artistName.text = model.artistName
        binding.genreValue.text = model.primaryGenreName
        binding.countryValue.text = model.country
        binding.yearValue.text = model.releaseDate.substringBefore("-")
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

    private fun playbackCases(state: PlayerState) {
        when(state) {
            PlayerState.PLAYING -> {
                binding.playButton.setImageResource(R.drawable.btn_pause)
                playbackProgressCounter(playerInteractor.getPlayerState())
            }
            PlayerState.PAUSED -> {
                binding.playButton.setImageResource(R.drawable.btn_play)
                handler.removeCallbacks(playbackRunnable)
            }
            PlayerState.PREPARED -> {
                handler.removeCallbacks(playbackRunnable)
                binding.playbackProgress.text = getString(R.string.zeroZero)
                binding.playButton.setImageResource(R.drawable.btn_play)
            }

            PlayerState.DEFAULT -> {
                binding.playButton.isEnabled = true
            }
        }
    }

    private fun playbackControl(state: PlayerState) {
        when(state) {
            PlayerState.PLAYING -> {
                playerInteractor.pausePlayer()
            }

            PlayerState.PREPARED, PlayerState.PAUSED -> {
                playerInteractor.startPlayer()
            }
            else -> {}
        }
    }

    private fun playbackProgressCounter(state: PlayerState) {
        if (state == PlayerState.PLAYING) {
            binding.playbackProgress.text = dateFormat.format(playerInteractor.getCurrentPosition())
            handler.postDelayed(playbackRunnable, 300)
        }
    }
}