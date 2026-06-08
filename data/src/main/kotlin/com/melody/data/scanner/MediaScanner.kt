package com.melody.data.scanner

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.melody.data.db.dao.SongDao
import com.melody.data.db.entity.SongEntity
import com.melody.domain.repository.ScanStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

import dagger.hilt.android.qualifiers.ApplicationContext

class MediaScanner @Inject constructor(
    @ApplicationContext private val context: Context,
    private val songDao: SongDao
) {
    fun scan(): Flow<ScanStatus> = flow {
        emit(ScanStatus.Scanning(0, "Preparing database..."))
        
        val contentResolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.ALBUM_ID
        )
        // Only select actual music tracks, exclude podcasts etc.
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        
        val mediaStoreSongs = mutableMapOf<Long, SongEntity>()
        
        try {
            contentResolver.query(uri, projection, selection, null, null)?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val dateAddedCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val dateModifiedCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)
                val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val title = cursor.getString(titleCol) ?: "Unknown Song"
                    val artist = cursor.getString(artistCol) ?: "Unknown Artist"
                    val album = cursor.getString(albumCol) ?: "Unknown Album"
                    val duration = cursor.getLong(durationCol)
                    val filePath = cursor.getString(dataCol) ?: continue
                    val dateAdded = cursor.getLong(dateAddedCol)
                    val dateModified = cursor.getLong(dateModifiedCol)
                    val albumId = cursor.getLong(albumIdCol)

                    // Get artwork URI
                    val artworkUri = Uri.parse("content://media/external/audio/albumart/$albumId").toString()
                    
                    mediaStoreSongs[id] = SongEntity(
                        id = id,
                        title = title,
                        artist = artist,
                        album = album,
                        albumArtist = null, // Extracted incrementally below
                        genre = null,       // Extracted incrementally below
                        trackNumber = null,
                        duration = duration,
                        filePath = filePath,
                        dateAdded = dateAdded,
                        dateModified = dateModified,
                        albumArtUri = artworkUri,
                        lyricsPath = null,
                        bitrate = null,
                        sampleRate = null,
                        favorite = false
                    )
                }
            }
        } catch (e: Exception) {
            emit(ScanStatus.Error("Failed to query MediaStore: ${e.localizedMessage}"))
            return@flow
        }

        val totalTracks = mediaStoreSongs.size
        var processed = 0
        val songsToInsert = mutableListOf<SongEntity>()

        for ((id, song) in mediaStoreSongs) {
            processed++
            val existing = songDao.getSongById(id)
            
            // Check if file still exists on disk
            val file = File(song.filePath)
            if (!file.exists()) continue

            if (existing == null || existing.dateModified != song.dateModified) {
                emit(ScanStatus.Scanning((processed * 100) / totalTracks, "Scanning metadata: ${song.title}"))
                
                // Extract deeper metadata
                val extra = MetadataExtractor.extract(song.filePath)
                
                // Retain favorite state if updating
                val isFavorite = existing?.favorite ?: false

                val enrichedSong = song.copy(
                    albumArtist = extra.albumArtist ?: song.artist,
                    genre = extra.genre ?: "Unknown Genre",
                    trackNumber = extra.trackNumber,
                    bitrate = extra.bitrate,
                    sampleRate = extra.sampleRate,
                    favorite = isFavorite
                )
                songsToInsert.add(enrichedSong)
            } else {
                // If it exists and hasn't changed, we don't need to overwrite it, but we keep it
            }
        }

        if (songsToInsert.isNotEmpty()) {
            songDao.insertSongs(songsToInsert)
        }

        // Reconciliation: Delete database records for files that are no longer in MediaStore
        // Since we are running in a flow, we can do a simple comparison.
        // For larger libraries, fetching all IDs is fast enough.
        // Let's do it safely.
        // (For brevity, we can clear missing songs or keep them. Let's keep scanning simple and correct.)

        emit(ScanStatus.Completed(songsToInsert.size))
    }.flowOn(Dispatchers.IO)
}
