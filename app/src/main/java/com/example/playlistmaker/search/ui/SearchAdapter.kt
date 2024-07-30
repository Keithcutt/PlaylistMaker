package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track

class SearchAdapter(
    private val onTrackClick: (track: Track) -> Unit
) : RecyclerView.Adapter<SearchViewHolder>() {

    private var tracks: List<Track> = emptyList()

    @SuppressWarnings("notifyDataSetChanged")
    fun setTracks(tracks: List<Track>) {
        this.tracks = tracks
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener{
            onTrackClick(tracks[position])
        }
    }

    override fun getItemCount(): Int = tracks.size
}