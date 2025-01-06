package com.example.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.data.db.entity.PlaylistTrackEntity

@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_tracks_table")
    suspend fun getTracksFromAllPlaylists(): List<PlaylistTrackEntity>

    @Query("DELETE FROM playlist_tracks_table WHERE trackId = :id")
    suspend fun deleteTrackById(id: Int)

    @Query("UPDATE playlist_tracks_table SET isfavourite = :isFavourite WHERE trackId = :trackId")
    suspend fun updateFavouriteStatus(trackId: Int, isFavourite: Boolean)

}