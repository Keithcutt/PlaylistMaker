package com.example.playlistmaker.media.ui.playlist_collection_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.media.presentation.model.PlaylistUIModel

class PlaylistCollectionAdapter(
    private val onPlaylistClick: (playlistId: Int) -> Unit
) :
    RecyclerView.Adapter<PlaylistCollectionViewHolder>() {

    private var playlists: List<PlaylistUIModel> = emptyList()

    @Suppress("notifyDataSetChanged")
    fun updatePlaylists(newPlaylists: List<PlaylistUIModel>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistCollectionViewHolder {
        val binding =
            PlaylistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistCollectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistCollectionViewHolder, position: Int) {
        val currentPlaylist = playlists[position]

        holder.bind(currentPlaylist)
        holder.itemView.setOnClickListener {
            onPlaylistClick(currentPlaylist.playlistId)
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}