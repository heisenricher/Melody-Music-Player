package com.melody.player.service

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

class MelodySessionCallback(
    private val context: Context
) : MediaLibraryService.MediaLibrarySession.Callback {

    @OptIn(UnstableApi::class)
    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: List<MediaItem>
    ): ListenableFuture<List<MediaItem>> {
        // Resolve media items before they are added to ExoPlayer
        val updatedItems = mediaItems.map { item ->
            item.buildUpon()
                .setUri(item.requestMetadata.mediaUri ?: item.localConfiguration?.uri)
                .build()
        }
        return Futures.immediateFuture(updatedItems)
    }
}
