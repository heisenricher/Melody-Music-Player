package com.melody.domain.repository

import com.melody.domain.model.Song
import com.melody.domain.model.Album
import com.melody.domain.model.Artist
import com.melody.domain.model.Genre
import com.melody.domain.model.FolderNode
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun getAllSongs(): Flow<List<Song>>
    fun getSongsByFolder(folderPath: String): Flow<List<Song>>
    fun getFavoriteSongs(): Flow<List<Song>>
    fun getRecentlyPlayedSongs(limit: Int): Flow<List<Song>>
    fun getMostPlayedSongs(limit: Int): Flow<List<Song>>
    
    fun getAlbums(): Flow<List<Album>>
    fun getSongsByAlbum(albumId: Long): Flow<List<Song>>
    
    fun getArtists(): Flow<List<Artist>>
    fun getSongsByArtist(artistName: String): Flow<List<Song>>
    
    fun getGenres(): Flow<List<Genre>>
    fun getSongsByGenre(genreName: String): Flow<List<Song>>
    
    fun getFolders(): Flow<List<FolderNode>>
    suspend fun pinFolder(folderPath: String, pin: Boolean)
    suspend fun excludeFolder(folderPath: String, exclude: Boolean)
    
    suspend fun toggleFavorite(songId: Long): Boolean
    suspend fun incrementPlayCount(songId: Long)
    suspend fun addToRecentlyPlayed(songId: Long)
    
    suspend fun updateSongTags(
        songId: Long,
        title: String,
        artist: String,
        album: String,
        genre: String,
        artworkPath: String?
    ): Boolean
    
    suspend fun scanMedia(): Flow<ScanStatus>
    
    suspend fun getLyrics(song: Song): Flow<String?>

    fun searchEverything(query: String): Flow<SearchResults>
}

sealed interface ScanStatus {
    object Idle : ScanStatus
    data class Scanning(val progress: Int, val currentFile: String) : ScanStatus
    data class Completed(val addedCount: Int) : ScanStatus
    data class Error(val message: String) : ScanStatus
}

data class SearchResults(
    val songs: List<Song>,
    val albums: List<Album>,
    val artists: List<Artist>,
    val genres: List<Genre>,
    val folders: List<FolderNode>
)
