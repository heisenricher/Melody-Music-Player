package com.melody.data.scanner

import android.media.MediaMetadataRetriever
import java.io.File

object MetadataExtractor {
    data class ExtraMetadata(
        val bitrate: Int?,
        val sampleRate: Int?,
        val lyrics: String?,
        val albumArtist: String?,
        val genre: String?,
        val trackNumber: Int?
    )

    fun extract(filePath: String): ExtraMetadata {
        val retriever = MediaMetadataRetriever()
        var bitrate: Int? = null
        var sampleRate: Int? = null
        var lyrics: String? = null
        var albumArtist: String? = null
        var genre: String? = null
        var trackNumber: Int? = null

        try {
            val file = File(filePath)
            if (file.exists()) {
                retriever.setDataSource(filePath)
                
                // Bitrate (in bps, convert to kbps)
                val bitrateStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
                if (!bitrateStr.isNullOrEmpty()) {
                    bitrate = bitrateStr.toIntOrNull()?.let { it / 1000 }
                }

                // Sample Rate (usually only available in API 31+ via METADATA_KEY_SAMPLERATE, otherwise fallback)
                val sampleRateStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_SAMPLERATE)
                if (!sampleRateStr.isNullOrEmpty()) {
                    sampleRate = sampleRateStr.toIntOrNull()
                }

                // Album Artist
                albumArtist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)

                // Genre
                genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)

                // Track Number (formats are sometimes "1/12" or "1", parse appropriately)
                val trackStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)
                if (!trackStr.isNullOrEmpty()) {
                    trackNumber = trackStr.split("/").firstOrNull()?.toIntOrNull()
                }

                // Embedded lyrics
                // Standard MediaMetadataRetriever cannot extract lyrics on all platforms.
                // We'll also check JAudioTagger in Repository tag editing / loading.
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                retriever.release()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        return ExtraMetadata(
            bitrate = bitrate,
            sampleRate = sampleRate,
            lyrics = lyrics,
            albumArtist = albumArtist,
            genre = genre,
            trackNumber = trackNumber
        )
    }
}
