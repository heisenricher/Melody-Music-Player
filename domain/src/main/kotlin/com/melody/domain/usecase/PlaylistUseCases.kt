package com.melody.domain.usecase

import com.melody.domain.model.Playlist
import com.melody.domain.model.Song
import com.melody.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlaylistsUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    operator fun invoke(): Flow<List<Playlist>> = repository.getAllPlaylists()
}

class GetSongsInPlaylistUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    operator fun invoke(playlistId: Long): Flow<List<Song>> = repository.getSongsInPlaylist(playlistId)
}

class CreatePlaylistUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(name: String): Long = repository.createPlaylist(name)
}

class RenamePlaylistUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: Long, newName: String) {
        repository.renamePlaylist(playlistId, newName)
    }
}

class DeletePlaylistUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: Long) {
        repository.deletePlaylist(playlistId)
    }
}

class AddSongsToPlaylistUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: Long, songIds: List<Long>) {
        repository.addSongsToPlaylist(playlistId, songIds)
    }
}

class RemoveSongsFromPlaylistUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: Long, songIds: List<Long>) {
        repository.removeSongsFromPlaylist(playlistId, songIds)
    }
}

class ImportM3UUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(filePath: String): Boolean = repository.importM3U(filePath)
}

class ExportM3UUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: Long, destinationPath: String): Boolean =
        repository.exportM3U(playlistId, destinationPath)
}
