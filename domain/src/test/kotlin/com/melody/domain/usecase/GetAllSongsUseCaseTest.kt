package com.melody.domain.usecase

import com.melody.domain.model.Song
import com.melody.domain.repository.SongRepository
import com.melody.domain.repository.ScanStatus
import com.melody.domain.repository.SearchResults
import com.melody.domain.model.Album
import com.melody.domain.model.Artist
import com.melody.domain.model.Genre
import com.melody.domain.model.FolderNode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetAllSongsUseCaseTest {

    @Test
    fun `invoke should return all songs from repository`() = runTest {
        val testSongs = listOf(
            Song(
                id = 1L,
                title = "Song One",
                artist = "Artist One",
                album = "Album One",
                albumArtist = null,
                genre = "Pop",
                trackNumber = 1,
                duration = 180000L,
                filePath = "/songs/1.mp3",
                dateAdded = 0,
                dateModified = 0,
                albumArtUri = null,
                lyricsPath = null,
                bitrate = null,
                sampleRate = null,
                favorite = false
            )
        )

        val repository = object : SongRepository {
            override fun getAllSongs(): Flow<List<Song>> = flowOf(testSongs)
            override fun getSongsByFolder(folderPath: String): Flow<List<Song>> = flowOf(emptyList())
            override fun getFavoriteSongs(): Flow<List<Song>> = flowOf(emptyList())
            override fun getRecentlyPlayedSongs(limit: Int): Flow<List<Song>> = flowOf(emptyList())
            override fun getMostPlayedSongs(limit: Int): Flow<List<Song>> = flowOf(emptyList())
            override fun getAlbums(): Flow<List<Album>> = flowOf(emptyList())
            override fun getSongsByAlbum(albumId: Long): Flow<List<Song>> = flowOf(emptyList())
            override fun getArtists(): Flow<List<Artist>> = flowOf(emptyList())
            override fun getSongsByArtist(artistName: String): Flow<List<Song>> = flowOf(emptyList())
            override fun getGenres(): Flow<List<Genre>> = flowOf(emptyList())
            override fun getSongsByGenre(genreName: String): Flow<List<Song>> = flowOf(emptyList())
            override fun getFolders(): Flow<List<FolderNode>> = flowOf(emptyList())
            override suspend fun pinFolder(folderPath: String, pin: Boolean) {}
            override suspend fun excludeFolder(folderPath: String, exclude: Boolean) {}
            override suspend fun toggleFavorite(songId: Long): Boolean = false
            override suspend fun incrementPlayCount(songId: Long) {}
            override suspend fun addToRecentlyPlayed(songId: Long) {}
            override suspend fun updateSongTags(songId: Long, title: String, artist: String, album: String, genre: String, artworkPath: String?): Boolean = false
            override suspend fun scanMedia(): Flow<ScanStatus> = flowOf(ScanStatus.Idle)
            override suspend fun getLyrics(song: Song): Flow<String?> = flowOf(null)
            override fun searchEverything(query: String): Flow<SearchResults> = flowOf(SearchResults(emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        }

        val useCase = GetAllSongsUseCase(repository)
        val result = useCase().first()

        assertEquals(1, result.size)
        assertEquals("Song One", result[0].title)
    }
}
