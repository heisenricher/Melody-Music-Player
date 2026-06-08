package com.melody.domain.usecase

import com.melody.domain.model.Song
import com.melody.domain.model.Album
import com.melody.domain.model.Artist
import com.melody.domain.model.Genre
import com.melody.domain.repository.SongRepository
import com.melody.domain.repository.ScanStatus
import com.melody.domain.repository.SearchResults
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSongsUseCase @Inject constructor(
    private val repository: SongRepository
) {
    operator fun invoke(): Flow<List<Song>> = repository.getAllSongs()
}

class GetSongsByFolderUseCase @Inject constructor(
    private val repository: SongRepository
) {
    operator fun invoke(folderPath: String): Flow<List<Song>> = repository.getSongsByFolder(folderPath)
}

class GetAlbumsUseCase @Inject constructor(
    private val repository: SongRepository
) {
    operator fun invoke(): Flow<List<Album>> = repository.getAlbums()
}

class GetSongsByAlbumUseCase @Inject constructor(
    private val repository: SongRepository
) {
    operator fun invoke(albumId: Long): Flow<List<Song>> = repository.getSongsByAlbum(albumId)
}

class GetArtistsUseCase @Inject constructor(
    private val repository: SongRepository
) {
    operator fun invoke(): Flow<List<Artist>> = repository.getArtists()
}

class GetSongsByArtistUseCase @Inject constructor(
    private val repository: SongRepository
) {
    operator fun invoke(artistName: String): Flow<List<Song>> = repository.getSongsByArtist(artistName)
}

class GetGenresUseCase @Inject constructor(
    private val repository: SongRepository
) {
    operator fun invoke(): Flow<List<Genre>> = repository.getGenres()
}

class GetSongsByGenreUseCase @Inject constructor(
    private val repository: SongRepository
) {
    operator fun invoke(genreName: String): Flow<List<Song>> = repository.getSongsByGenre(genreName)
}

class GetFavoritesUseCase @Inject constructor(
    private val repository: SongRepository
) {
    operator fun invoke(): Flow<List<Song>> = repository.getFavoriteSongs()
}

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: SongRepository
) {
    suspend operator fun invoke(songId: Long): Boolean = repository.toggleFavorite(songId)
}

class GetRecentlyPlayedUseCase @Inject constructor(
    private val repository: SongRepository
) {
    operator fun invoke(limit: Int = 50): Flow<List<Song>> = repository.getRecentlyPlayedSongs(limit)
}

class GetMostPlayedUseCase @Inject constructor(
    private val repository: SongRepository
) {
    operator fun invoke(limit: Int = 50): Flow<List<Song>> = repository.getMostPlayedSongs(limit)
}

class ScanMediaUseCase @Inject constructor(
    private val repository: SongRepository
) {
    suspend operator fun invoke(): Flow<ScanStatus> = repository.scanMedia()
}

class GetLyricsUseCase @Inject constructor(
    private val repository: SongRepository
) {
    suspend operator fun invoke(song: Song): Flow<String?> = repository.getLyrics(song)
}

class UpdateSongTagsUseCase @Inject constructor(
    private val repository: SongRepository
) {
    suspend operator fun invoke(
        songId: Long,
        title: String,
        artist: String,
        album: String,
        genre: String,
        artworkPath: String?
    ): Boolean = repository.updateSongTags(songId, title, artist, album, genre, artworkPath)
}

class SearchUseCase @Inject constructor(
    private val repository: SongRepository
) {
    operator fun invoke(query: String): Flow<SearchResults> = repository.searchEverything(query)
}
