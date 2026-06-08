package com.melody.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.melody.data.db.entity.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM songs ORDER BY title ASC")
    fun getAllSongs(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE filePath LIKE :folderPath || '%'")
    fun getSongsByFolder(folderPath: String): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE favorite = 1 ORDER BY title ASC")
    fun getFavoriteSongs(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs INNER JOIN recently_played ON songs.id = recently_played.songId ORDER BY recently_played.playedAt DESC LIMIT :limit")
    fun getRecentlyPlayed(limit: Int): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs INNER JOIN play_counts ON songs.id = play_counts.songId ORDER BY play_counts.count DESC LIMIT :limit")
    fun getMostPlayed(limit: Int): Flow<List<SongEntity>>

    // Dynamic Projections
    @Query("SELECT MIN(id) as id, album as name, artist as artist, MIN(albumArtUri) as artwork FROM songs GROUP BY album ORDER BY album ASC")
    fun getAlbums(): Flow<List<AlbumProjection>>

    @Query("SELECT * FROM songs WHERE album = (SELECT album FROM songs WHERE id = :albumId) ORDER BY trackNumber ASC, title ASC")
    fun getSongsByAlbumId(albumId: Long): Flow<List<SongEntity>>

    @Query("SELECT MIN(id) as id, artist as name, MIN(albumArtUri) as artwork FROM songs GROUP BY artist ORDER BY artist ASC")
    fun getArtists(): Flow<List<ArtistProjection>>

    @Query("SELECT * FROM songs WHERE artist = :artistName ORDER BY title ASC")
    fun getSongsByArtist(artistName: String): Flow<List<SongEntity>>

    @Query("SELECT genre as name, COUNT(*) as songCount FROM songs WHERE genre IS NOT NULL AND genre != '' GROUP BY genre ORDER BY genre ASC")
    fun getGenres(): Flow<List<GenreProjection>>

    @Query("SELECT * FROM songs WHERE genre = :genreName ORDER BY title ASC")
    fun getSongsByGenre(genreName: String): Flow<List<SongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)

    @Query("DELETE FROM songs WHERE id = :songId")
    suspend fun deleteSong(songId: Long)

    @Query("UPDATE songs SET favorite = NOT favorite WHERE id = :songId")
    suspend fun toggleFavorite(songId: Long): Int

    @Query("SELECT favorite FROM songs WHERE id = :songId")
    suspend fun isFavorite(songId: Long): Boolean

    @Query("UPDATE songs SET title = :title, artist = :artist, album = :album, genre = :genre, albumArtUri = :artworkPath WHERE id = :songId")
    suspend fun updateSongTags(songId: Long, title: String, artist: String, album: String, genre: String, artworkPath: String?)

    @Query("SELECT * FROM songs WHERE id = :songId")
    suspend fun getSongById(songId: Long): SongEntity?

    @Query("SELECT * FROM songs WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%' OR album LIKE '%' || :query || '%'")
    fun searchSongs(query: String): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE filePath = :filePath LIMIT 1")
    suspend fun getSongByPath(filePath: String): SongEntity?

    @Query("DELETE FROM songs")
    suspend fun clearAll()
}

data class AlbumProjection(
    val id: Long,
    val name: String,
    val artist: String,
    val artwork: String?
)

data class ArtistProjection(
    val id: Long,
    val name: String,
    val artwork: String?
)

data class GenreProjection(
    val name: String,
    val songCount: Int
)
