package com.melody.player.service

import android.content.Context
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionCommands
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

const val COMMAND_SEEK_BACK_10 = "SEEK_BACK_10"
const val COMMAND_SEEK_FORWARD_10 = "SEEK_FORWARD_10"

class MelodySessionCallback(
    private val context: Context
) : MediaLibraryService.MediaLibrarySession.Callback {

    @OptIn(UnstableApi::class)
    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): MediaSession.ConnectionResult {
        val connectionResult = super.onConnect(session, controller)
        val sessionCommands = connectionResult.availableSessionCommands.buildUpon()
            .add(SessionCommand(COMMAND_SEEK_BACK_10, Bundle.EMPTY))
            .add(SessionCommand(COMMAND_SEEK_FORWARD_10, Bundle.EMPTY))
            .build()
        return MediaSession.ConnectionResult.accept(
            sessionCommands,
            connectionResult.availablePlayerCommands
        )
    }

    @OptIn(UnstableApi::class)
    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {
        return when (customCommand.customAction) {
            COMMAND_SEEK_BACK_10 -> {
                session.player.seekBack()
                Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
            }
            COMMAND_SEEK_FORWARD_10 -> {
                session.player.seekForward()
                Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
            }
            else -> super.onCustomCommand(session, controller, customCommand, args)
        }
    }

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
