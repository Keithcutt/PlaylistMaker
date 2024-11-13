package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track

class TracksAdapter(
    private val onTrackClick: (track: Track) -> Unit
) : RecyclerView.Adapter<TracksViewHolder>() {

    private var tracks: List<Track> = emptyList()

    @SuppressWarnings("notifyDataSetChanged")
    fun setTracks(tracks: List<Track>) {
        this.tracks = tracks
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TracksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener{
            onTrackClick(tracks[position])
        }
    }

    override fun getItemCount(): Int = tracks.size
}