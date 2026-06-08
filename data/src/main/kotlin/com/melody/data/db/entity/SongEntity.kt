package com.melody.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.melody.domain.model.Song

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumArtist: String?,
    val genre: String?,
    val trackNumber: Int?,
    val duration: Long,
    val filePath: String,
    val dateAdded: Long,
    val dateModified: Long,
    val albumArtUri: String?,
    val lyricsPath: String?,
    val bitrate: Int?,
    val sampleRate: Int?,
    val favorite: Boolean
) {
    fun toDomain(): Song = Song(
        id = id,
        title = title,
        artist = artist,
        album = album,
        albumArtist = albumArtist,
        genre = genre,
        trackNumber = trackNumber,
        duration = duration,
        filePath = filePath,
        dateAdded = dateAdded,
        dateModified = dateModified,
        albumArtUri = albumArtUri,
        lyricsPath = lyricsPath,
        bitrate = bitrate,
        sampleRate = sampleRate,
        favorite = favorite
    )

    companion object {
        fun fromDomain(song: Song): SongEntity = SongEntity(
            id = song.id,
            title = song.title,
            artist = song.artist,
            album = song.album,
            albumArtist = song.albumArtist,
            genre = song.genre,
            trackNumber = song.trackNumber,
            duration = song.duration,
            filePath = song.filePath,
            dateAdded = song.dateAdded,
            dateModified = song.dateModified,
            albumArtUri = song.albumArtUri,
            lyricsPath = song.lyricsPath,
            bitrate = song.bitrate,
            sampleRate = song.sampleRate,
            favorite = song.favorite
        )
    }
}
