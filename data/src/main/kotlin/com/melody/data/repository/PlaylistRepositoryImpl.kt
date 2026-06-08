package com.melody.data.repository

import com.melody.data.db.dao.PlaylistDao
import com.melody.data.db.dao.SongDao
import com.melody.data.db.entity.PlaylistEntity
import com.melody.data.db.entity.PlaylistSongEntity
import com.melody.domain.model.Playlist
import com.melody.domain.model.Song
import com.melody.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val songDao: SongDao
) : PlaylistRepository {

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map { list -> list.map { it.toDomain() } }
    }

    override fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>> {
        return playlistDao.getSongsInPlaylist(playlistId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun createPlaylist(name: String): Long {
        val entity = PlaylistEntity(
            name = name,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return playlistDao.insertPlaylist(entity)
    }

    override suspend fun renamePlaylist(playlistId: Long, newName: String) {
        playlistDao.renamePlaylist(playlistId, newName, System.currentTimeMillis())
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylist(playlistId)
    }

    override suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>) {
        val maxPos = playlistDao.getMaxPosition(playlistId) ?: 0
        var position = maxPos + 1
        val playlistSongs = songIds.map { songId ->
            PlaylistSongEntity(playlistId, songId, position++)
        }
        playlistDao.insertPlaylistSongs(playlistSongs)
    }

    override suspend fun removeSongsFromPlaylist(playlistId: Long, songIds: List<Long>) {
        playlistDao.removeSongsFromPlaylist(playlistId, songIds)
    }

    override suspend fun importM3U(filePath: String): Boolean {
        val file = File(filePath)
        if (!file.exists() || !file.isFile) return false

        return try {
            val lines = file.readLines()
            if (lines.isEmpty()) return false

            val playlistName = file.nameWithoutExtension
            val playlistId = createPlaylist(playlistName)

            val songPaths = mutableListOf<String>()
            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.isEmpty() || trimmed.startsWith("#")) continue
                // M3U files contain direct file paths or URIs
                songPaths.add(trimmed)
            }

            val songIds = mutableListOf<Long>()
            for (path in songPaths) {
                // Find matching song in database by path
                val song = songDao.getSongByPath(path)
                if (song != null) {
                    songIds.add(song.id)
                }
            }

            if (songIds.isNotEmpty()) {
                addSongsToPlaylist(playlistId, songIds)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun exportM3U(playlistId: Long, destinationPath: String): Boolean {
        val playlist = playlistDao.getPlaylistById(playlistId) ?: return false
        val destFile = File(destinationPath)
        
        return try {
            val parent = destFile.parentFile
            if (parent != null && !parent.exists()) {
                parent.mkdirs()
            }
            
            playlistDao.getSongsInPlaylist(playlistId).map { list ->
                val builder = StringBuilder()
                builder.append("#EXTM3U\n")
                builder.append("#PLAYLIST:${playlist.name}\n\n")
                for (song in list) {
                    builder.append("#EXTINF:${song.duration / 1000},${song.artist} - ${song.title}\n")
                    builder.append("${song.filePath}\n")
                }
                destFile.writeText(builder.toString())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
