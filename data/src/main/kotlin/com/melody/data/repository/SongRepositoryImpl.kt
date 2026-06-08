package com.melody.data.repository

import com.melody.data.db.dao.SongDao
import com.melody.data.db.dao.PlayCountDao
import com.melody.data.db.dao.RecentlyPlayedDao
import com.melody.data.db.entity.SongEntity
import com.melody.data.db.entity.RecentlyPlayedEntity
import com.melody.data.scanner.MediaScanner
import com.melody.domain.model.Song
import com.melody.domain.model.Album
import com.melody.domain.model.Artist
import com.melody.domain.model.Genre
import com.melody.domain.model.FolderNode
import com.melody.domain.repository.SongRepository
import com.melody.domain.repository.ScanStatus
import com.melody.domain.repository.SearchResults
import com.melody.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.images.AndroidArtwork
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongRepositoryImpl @Inject constructor(
    private val songDao: SongDao,
    private val playCountDao: PlayCountDao,
    private val recentlyPlayedDao: RecentlyPlayedDao,
    private val mediaScanner: MediaScanner,
    private val settingsRepository: SettingsRepository
) : SongRepository {

    override fun getAllSongs(): Flow<List<Song>> {
        return combine(
            songDao.getAllSongs(),
            settingsRepository.getExcludedFolders()
        ) { songs, excluded ->
            songs.filter { song ->
                excluded.none { ext -> song.filePath.startsWith(ext) }
            }.map { it.toDomain() }
        }
    }

    override fun getSongsByFolder(folderPath: String): Flow<List<Song>> {
        return songDao.getSongsByFolder(folderPath).map { list -> list.map { it.toDomain() } }
    }

    override fun getFavoriteSongs(): Flow<List<Song>> {
        return songDao.getFavoriteSongs().map { list -> list.map { it.toDomain() } }
    }

    override fun getRecentlyPlayedSongs(limit: Int): Flow<List<Song>> {
        return songDao.getRecentlyPlayed(limit).map { list -> list.map { it.toDomain() } }
    }

    override fun getMostPlayedSongs(limit: Int): Flow<List<Song>> {
        return songDao.getMostPlayed(limit).map { list -> list.map { it.toDomain() } }
    }

    override fun getAlbums(): Flow<List<Album>> {
        return songDao.getAlbums().map { list ->
            list.map { Album(it.id, it.name, it.artist, it.artwork) }
        }
    }

    override fun getSongsByAlbum(albumId: Long): Flow<List<Song>> {
        return songDao.getSongsByAlbumId(albumId).map { list -> list.map { it.toDomain() } }
    }

    override fun getArtists(): Flow<List<Artist>> {
        return songDao.getArtists().map { list ->
            list.map { Artist(it.id, it.name, it.artwork) }
        }
    }

    override fun getSongsByArtist(artistName: String): Flow<List<Song>> {
        return songDao.getSongsByArtist(artistName).map { list -> list.map { it.toDomain() } }
    }

    override fun getGenres(): Flow<List<Genre>> {
        return songDao.getGenres().map { list ->
            list.map { Genre(it.name, it.songCount) }
        }
    }

    override fun getSongsByGenre(genreName: String): Flow<List<Song>> {
        return songDao.getSongsByGenre(genreName).map { list -> list.map { it.toDomain() } }
    }

    override fun getFolders(): Flow<List<FolderNode>> {
        return combine(
            songDao.getAllSongs(),
            settingsRepository.getPinnedFolders(),
            settingsRepository.getExcludedFolders()
        ) { songs, pinned, excluded ->
            val foldersMap = mutableMapOf<String, Pair<Int, String>>() // Path -> Pair(Count, Name)
            songs.forEach { song ->
                val parentFile = File(song.filePath).parentFile
                if (parentFile != null) {
                    val path = parentFile.absolutePath
                    val current = foldersMap[path] ?: Pair(0, parentFile.name)
                    foldersMap[path] = Pair(current.first + 1, current.second)
                }
            }
            foldersMap.map { (path, info) ->
                FolderNode(
                    name = info.second,
                    path = path,
                    subFoldersCount = 0, // Simplified for performance
                    songsCount = info.first,
                    isPinned = pinned.contains(path),
                    isExcluded = excluded.contains(path)
                )
            }.sortedWith(compareByDescending<FolderNode> { it.isPinned }.thenBy { it.name })
        }
    }

    override suspend fun pinFolder(folderPath: String, pin: Boolean) {
        if (pin) {
            settingsRepository.addPinnedFolder(folderPath)
        } else {
            settingsRepository.removePinnedFolder(folderPath)
        }
    }

    override suspend fun excludeFolder(folderPath: String, exclude: Boolean) {
        if (exclude) {
            settingsRepository.addExcludedFolder(folderPath)
        } else {
            settingsRepository.removeExcludedFolder(folderPath)
        }
    }

    override suspend fun toggleFavorite(songId: Long): Boolean {
        songDao.toggleFavorite(songId)
        return songDao.isFavorite(songId)
    }

    override suspend fun incrementPlayCount(songId: Long) {
        playCountDao.incrementPlayCount(songId)
    }

    override suspend fun addToRecentlyPlayed(songId: Long) {
        recentlyPlayedDao.insertRecentlyPlayed(RecentlyPlayedEntity(songId, System.currentTimeMillis()))
    }

    override suspend fun updateSongTags(
        songId: Long,
        title: String,
        artist: String,
        album: String,
        genre: String,
        artworkPath: String?
    ): Boolean {
        val songEntity = songDao.getSongById(songId) ?: return false
        val file = File(songEntity.filePath)
        if (!file.exists() || !file.canWrite()) return false
        
        return try {
            val audioFile = AudioFileIO.read(file)
            val tag = audioFile.tagOrCreateAndSetDefault
            tag.setField(FieldKey.TITLE, title)
            tag.setField(FieldKey.ARTIST, artist)
            tag.setField(FieldKey.ALBUM, album)
            tag.setField(FieldKey.GENRE, genre)
            
            if (artworkPath != null) {
                val artFile = File(artworkPath)
                if (artFile.exists()) {
                    val artwork = AndroidArtwork.createArtworkFromFile(artFile)
                    tag.deleteArtworkField()
                    tag.setField(artwork)
                }
            }
            audioFile.commit()
            
            // Sync with DB
            songDao.updateSongTags(songId, title, artist, album, genre, songEntity.albumArtUri)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun scanMedia(): Flow<ScanStatus> = mediaScanner.scan()

    override suspend fun getLyrics(song: Song): Flow<String?> = flow {
        // 1. Try local .lrc file in same directory
        val songFile = File(song.filePath)
        val lrcFile = File(songFile.parent, songFile.nameWithoutExtension + ".lrc")
        if (lrcFile.exists()) {
            emit(lrcFile.readText())
            return@flow
        }

        // 2. Try embedded tag
        try {
            val audioFile = AudioFileIO.read(songFile)
            val tag = audioFile.tag
            if (tag != null) {
                val embeddedLyrics = tag.getFirst(FieldKey.LYRICS)
                if (!embeddedLyrics.isNullOrEmpty()) {
                    emit(embeddedLyrics)
                    return@flow
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        emit(null)
    }

    override fun searchEverything(query: String): Flow<SearchResults> {
        if (query.isEmpty()) {
            return flow { emit(SearchResults(emptyList(), emptyList(), emptyList(), emptyList(), emptyList())) }
        }
        val songsFlow = songDao.searchSongs(query).map { list -> list.map { it.toDomain() } }
        val albumsFlow = getAlbums().map { list -> list.filter { it.name.contains(query, ignoreCase = true) } }
        val artistsFlow = getArtists().map { list -> list.filter { it.name.contains(query, ignoreCase = true) } }
        val genresFlow = getGenres().map { list -> list.filter { it.name.contains(query, ignoreCase = true) } }
        val foldersFlow = getFolders().map { list -> list.filter { it.name.contains(query, ignoreCase = true) } }

        return combine(songsFlow, albumsFlow, artistsFlow, genresFlow, foldersFlow) { songs, albums, artists, genres, folders ->
            SearchResults(songs, albums, artists, genres, folders)
        }
    }
}
