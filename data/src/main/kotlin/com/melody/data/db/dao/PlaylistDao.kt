package com.melody.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.melody.data.db.entity.PlaylistEntity
import com.melody.data.db.entity.PlaylistSongEntity
import com.melody.data.db.entity.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY name ASC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun getPlaylistById(id: Long): PlaylistEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("UPDATE playlists SET name = :newName, updatedAt = :updatedAt WHERE id = :playlistId")
    suspend fun renamePlaylist(playlistId: Long, newName: String, updatedAt: Long)

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistSongs(playlistSongs: List<PlaylistSongEntity>)

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND songId IN (:songIds)")
    suspend fun removeSongsFromPlaylist(playlistId: Long, songIds: List<Long>)

    @Query("SELECT * FROM songs INNER JOIN playlist_songs ON songs.id = playlist_songs.songId WHERE playlist_songs.playlistId = :playlistId ORDER BY playlist_songs.position ASC")
    fun getSongsInPlaylist(playlistId: Long): Flow<List<SongEntity>>

    @Query("SELECT MAX(position) FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun getMaxPosition(playlistId: Long): Int?
}
