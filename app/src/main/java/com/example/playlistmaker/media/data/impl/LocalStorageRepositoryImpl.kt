package com.example.playlistmaker.media.data.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.media.domain.local_storage_api.LocalStorageRepository
import java.io.File
import java.io.FileOutputStream

class LocalStorageRepositoryImpl(private val appContext: Context) : LocalStorageRepository {

    companion object {
        private const val ALBUM_NAME = "playlistsAlbum"
        private const val COVER_QUALITY_30 = 30
    }

    override fun savePlaylistCover(uri: String) {

        val filePath =
            File(appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), ALBUM_NAME)

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val fileName = "playlist_cover_${uri.takeLast(10)}.jpg"
        val file = File(filePath, fileName)

        val inputStream = appContext.contentResolver.openInputStream(Uri.parse(uri))
        val outputStream = FileOutputStream(file)

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, COVER_QUALITY_30, outputStream)

        inputStream?.close()
        outputStream.close()
    }
}