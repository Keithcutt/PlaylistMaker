package com.example.playlistmaker.search.ui

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val cover: ImageView = itemView.findViewById(R.id.track_cover)
    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val artistName: TextView = itemView.findViewById(R.id.artist_name)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)

    fun bind(currentTrack: Track) {

        Glide.with(itemView)
            .load(currentTrack.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2.toFloat(), itemView.context)))
            .placeholder(R.drawable.ic_placeholder_35x35)
            .into(cover)

        trackName.text = currentTrack.trackName
        artistName.text = currentTrack.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentTrack.trackTime)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}