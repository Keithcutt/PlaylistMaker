package com.example.playlistmaker.media.ui.playlists

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.media.presentation.model.PlaylistUIModel

class PlaylistsViewHolder(private val binding: PlaylistItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: PlaylistUIModel) {
        binding.playlistName.text = playlist.playlistName
        binding.playlistTrackCount.text = itemView.context.resources.getQuantityString(
            R.plurals.playlist_track_count,
            playlist.trackCount,
            playlist.trackCount
        )

        val playlistCover = playlist.coverUri

        Glide.with(itemView)
            .load(playlistCover)
            .centerCrop()
            .placeholder(R.drawable.ic_placeholder_104x103)
            .into(binding.playlistCover)
    }
}