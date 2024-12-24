package com.example.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.data.db.entity.FavouriteTrackEntity

@Dao
interface FavouriteTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: FavouriteTrackEntity)

    @Delete
    suspend fun deleteTrack(track: FavouriteTrackEntity)

    @Query("SELECT * FROM favourites_table ORDER BY timestamp DESC")
    suspend fun getFavouriteTracks(): List<FavouriteTrackEntity>

    @Query("SELECT trackId FROM favourites_table")
    suspend fun getFavouriteIDs(): List<Int>
}