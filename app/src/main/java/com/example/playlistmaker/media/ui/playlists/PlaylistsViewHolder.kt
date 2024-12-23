package com.example.playlistmaker.media.ui.playlists

import android.os.Environment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.media.domain.model.Playlist
import java.io.File

class PlaylistsViewHolder(private val binding: PlaylistItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        binding.playlistName.text = playlist.playlistName
        binding.playlistTrackCount.text = itemView.context.resources.getQuantityString(
            R.plurals.playlist_track_count,
            playlist.trackCount,
            playlist.trackCount
        )

        val playlistCover = playlist.coverUri
        if (playlistCover.isNullOrEmpty()) {
            binding.playlistCover.setImageResource(R.drawable.ic_placeholder_104x103)
        } else {

            val filePath = File(
                itemView.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "playlistsAlbum"
            )
            val fileName = "playlist_cover_${playlist.coverUri.takeLast(10)}.jpg"
            val file = File(filePath, fileName)

            Glide.with(itemView)
                .load(file)
                .centerCrop()
                .into(binding.playlistCover)
        }
    }
}