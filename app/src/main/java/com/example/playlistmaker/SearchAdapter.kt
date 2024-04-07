package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class SearchAdapter(
    private val tracks: List<Track>,
    private val trackToSave: TrackToSave
) : RecyclerView.Adapter<SearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener{
            Toast.makeText(holder.itemView.context, "Сохранение трека в список", Toast.LENGTH_SHORT).show() // Удалить

            trackToSave.onTrackClickListener(tracks[position])
        }
    }

    override fun getItemCount(): Int = tracks.size

    fun interface TrackToSave {
        fun onTrackClickListener(track: Track)
    }
}