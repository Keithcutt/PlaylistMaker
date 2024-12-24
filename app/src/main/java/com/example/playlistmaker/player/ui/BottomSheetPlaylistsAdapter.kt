package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlaylistItemPlayerScreenBinding
import com.example.playlistmaker.media.presentation.model.PlaylistUIModel

class BottomSheetPlaylistsAdapter(
    private val onPlaylistClick: (playlist: PlaylistUIModel) -> Unit
) : RecyclerView.Adapter<BottomSheetPlaylistsViewHolder>() {

    private var playlists: List<PlaylistUIModel> = emptyList()

    @Suppress("notifyDataSetChanged")
    fun updatePlaylists(newPlaylists: List<PlaylistUIModel>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BottomSheetPlaylistsViewHolder {
        val binding = PlaylistItemPlayerScreenBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BottomSheetPlaylistsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BottomSheetPlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            onPlaylistClick(playlists[position])
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}