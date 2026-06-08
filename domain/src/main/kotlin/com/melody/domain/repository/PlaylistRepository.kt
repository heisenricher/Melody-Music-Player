package com.melody.domain.repository

import com.melody.domain.model.Playlist
import com.melody.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>>
    
    suspend fun createPlaylist(name: String): Long
    suspend fun renamePlaylist(playlistId: Long, newName: String)
    suspend fun deletePlaylist(playlistId: Long)
    
    suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>)
    suspend fun removeSongsFromPlaylist(playlistId: Long, songIds: List<Long>)
    
    suspend fun importM3U(filePath: String): Boolean
    suspend fun exportM3U(playlistId: Long, destinationPath: String): Boolean
}
