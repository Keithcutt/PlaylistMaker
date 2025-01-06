package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.sharing.domain.model.EmailData
import com.example.playlistmaker.sharing.domain.navigator.ExternalNavigator
import java.text.SimpleDateFormat
import java.util.Locale

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    override fun shareLink() {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getShareAppLink())
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(intent, null)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    private fun getShareAppLink(): String {
        return context.getString(R.string.share_text)
    }

    override fun sharePlaylist(playlist: Playlist, tracksFromPlaylist: List<Track>) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, buildPlaylistInfoMessage(playlist, tracksFromPlaylist))
            type = "text/plain"
        }

        val sharePlaylistIntent =
            Intent.createChooser(intent, getChooserTitleText())
        sharePlaylistIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(sharePlaylistIntent)
    }

    private fun buildPlaylistInfoMessage(
        playlist: Playlist,
        tracksFromPlaylist: List<Track>
    ): String {
        var message = "${playlist.playlistName}\n"

        if (playlist.description != null) {
            message += "${playlist.description}\n"
        }

        message += context.resources.getQuantityString(
            R.plurals.playlist_track_count,
            playlist.trackCount,
            playlist.trackCount
        ) + "\n\n"

        for (i in tracksFromPlaylist.indices) {
            val trackTimeMmSs =
                SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(tracksFromPlaylist[i].trackTime)
                    .toString()

            message += "${i + 1}. ${tracksFromPlaylist[i].artistName} - ${tracksFromPlaylist[i].trackName} " + "($trackTimeMmSs)\n"
        }

        return message
    }

    private fun getChooserTitleText(): String {
        return context.getString(R.string.share_playlist)
    }

    override fun openEmail() {
        Intent().apply {
            action = Intent.ACTION_SENDTO
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, getSupportEmailData().recipient)
            putExtra(Intent.EXTRA_SUBJECT, getSupportEmailData().subject)
            putExtra(Intent.EXTRA_TEXT, getSupportEmailData().text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(this)
        }


    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            recipient = arrayListOf(context.getString(R.string.email_recipient)),
            subject = context.getString(R.string.email_subject),
            text = context.getString(R.string.email_text)
        )
    }

    override fun openLink() {
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(getTermsLink())
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(this)
        }
    }

    private fun getTermsLink(): String {
        return context.getString(R.string.offer_link)
    }
}