package com.melody.player.service

import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.melody.domain.repository.SongRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MelodyPlaybackService : MediaLibraryService() {

    private var mediaSession: MediaLibrarySession? = null
    private lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var songRepository: SongRepository

    @Inject
    lateinit var equalizerController: com.melody.player.equalizer.EqualizerController

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        
        // Setup audio attributes for automatic Audio Focus Handling
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        exoPlayer = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true) // True enables audio focus handling
            .setHandleAudioBecomingNoisy(true)        // Pause on headset disconnected
            .setWakeMode(C.WAKE_MODE_LOCAL)            // Prevent system sleeping
            .build()

        equalizerController.initEffects(exoPlayer.audioSessionId)

        exoPlayer.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                if (mediaItem != null) {
                    val songId = mediaItem.mediaId.toLongOrNull()
                    if (songId != null) {
                        serviceScope.launch {
                            songRepository.addToRecentlyPlayed(songId)
                            songRepository.incrementPlayCount(songId)
                        }
                    }
                }
            }
        })

        val callback = MelodySessionCallback(this)

        mediaSession = MediaLibrarySession.Builder(this, exoPlayer, callback)
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaSession
    }

    override fun onDestroy() {
        serviceScope.cancel()
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
