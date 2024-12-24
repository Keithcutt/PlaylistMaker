package com.example.playlistmaker.player.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemPlayerScreenBinding
import com.example.playlistmaker.media.presentation.model.PlaylistUIModel

class BottomSheetPlaylistsViewHolder(private val binding: PlaylistItemPlayerScreenBinding) :
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
            .placeholder(R.drawable.ic_placeholder_35x35)
            .into(binding.playlistCover)

    }
}
