package com.example.playlistmaker.ui.player

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.api.OnPlayerStateChangeListener
import com.example.playlistmaker.domain.models.PlayerState
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private companion object {
        const val TRACK_KEY = "track"
        const val ARTWORK_CORNER_RADIUS = 8

//        const val STATE_DEFAULT = 0
//        const val STATE_PREPARED = 1
//        const val STATE_PLAYING = 2
//        const val STATE_PAUSED = 3
    }

    private lateinit var binding: ActivityPlayerBinding
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

//    private var playerState = STATE_DEFAULT
//    private var mediaPlayer = MediaPlayer()

    private val handler = Handler(Looper.getMainLooper())
    private val playbackRunnable = { playbackProgressCounter() }

    private val playerInteractor = Creator.provideInteractor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentTrack = getCurrentTrack()
        bindData(currentTrack)

        binding.backButton.setOnClickListener { finish() }
        // preparePlayer(currentTrack.previewUrl)
        playerInteractor.preparePlayer(currentTrack.previewUrl)
        binding.playButton.setOnClickListener {
            // playbackControl()
            playerInteractor.setOnPlayerStateChangeListener(object : OnPlayerStateChangeListener {
                override fun onChange(state: PlayerState) {
                    when (state) {
                        PlayerState.PREPARED -> {
                            binding.playButton.isEnabled = true
                            binding.playButton.setImageResource(R.drawable.btn_play)

                            handler.removeCallbacks(playbackRunnable)
                            binding.playbackProgress.text = getString(R.string.zeroZero)
                        }
                        PlayerState.PLAYING -> {
                            playerInteractor.startPlayer()
                            binding.playButton.setImageResource(R.drawable.btn_pause)
                            playbackProgressCounter()
                        }
                        PlayerState.PAUSED -> {
                            playerInteractor.pausePlayer()
                            binding.playButton.setImageResource(R.drawable.btn_play)
                            handler.removeCallbacks(playbackRunnable)
                        }

                        else -> {}
                    }
                }
            })
        }


    }

    override fun onPause() {
        super.onPause()
        // pausePlayer()
        playerInteractor.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        // mediaPlayer.release()
        playerInteractor.releasePlayer()

        handler.removeCallbacks(playbackRunnable)
    }

    private fun getCurrentTrack(): Track {
        val json = intent.getStringExtra(TRACK_KEY)
        return Gson().fromJson(json, Track::class.java)
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

//    private fun preparePlayer(url: String) {
        // mediaPlayer.setDataSource(url)
        // mediaPlayer.prepareAsync()
//        mediaPlayer.setOnPreparedListener {
//            binding.playButton.isEnabled = true
//            playerState = STATE_PREPARED
//        }
//        mediaPlayer.setOnCompletionListener {
//            binding.playButton.setImageResource(R.drawable.btn_play)
//            playerState = STATE_PREPARED
//
//            handler.removeCallbacks(playbackRunnable)
//            binding.playbackProgress.text = getString(R.string.zeroZero)
//        }
//    }

//    private fun startPlayer() {
//        mediaPlayer.start()
//        binding.playButton.setImageResource(R.drawable.btn_pause)
//        playerState = STATE_PLAYING
//        playbackProgressCounter()
//    }

//    private fun pausePlayer() {
//        mediaPlayer.pause()
//        binding.playButton.setImageResource(R.drawable.btn_play)
//        playerState = STATE_PAUSED
//        handler.removeCallbacks(playbackRunnable)
//    }

//    private fun playbackControl() {
//        when(playerState) {
//            STATE_PLAYING -> {
//                pausePlayer()
//            }
//            STATE_PREPARED, STATE_PAUSED -> {
//                startPlayer()
//            }
//        }
//    }

    private fun playbackProgressCounter() {
        // if (playerState == STATE_PLAYING) {
            binding.playbackProgress.text = dateFormat.format(playerInteractor.getCurrentPosition())
            handler.postDelayed(playbackRunnable, 300)
        // }
    }
}