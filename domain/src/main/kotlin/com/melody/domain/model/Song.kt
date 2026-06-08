package com.melody.domain.model

/**
 * Domain model representing a single local audio track.
 */
data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumArtist: String?,
    val genre: String?,
    val trackNumber: Int?,
    val duration: Long, // in milliseconds
    val filePath: String,
    val dateAdded: Long,
    val dateModified: Long,
    val albumArtUri: String?,
    val lyricsPath: String?,
    val bitrate: Int?, // in kbps
    val sampleRate: Int?, // in Hz
    val favorite: Boolean
)
