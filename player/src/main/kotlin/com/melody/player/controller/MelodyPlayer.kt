package com.melody.player.controller

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.melody.domain.model.PlayerState
import com.melody.domain.model.RepeatMode
import com.melody.domain.model.Song
import com.melody.player.equalizer.EqualizerController
import com.melody.player.service.MelodyPlaybackService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MelodyPlayer @Inject constructor(
    private val context: Context,
    private val equalizerController: EqualizerController
) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    private val playerScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressJob: Job? = null

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private var originalQueueList = listOf<Song>()

    init {
        initializeController()
    }

    private fun initializeController() {
        val sessionToken = SessionToken(context, ComponentName(context, MelodyPlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            try {
                mediaController = controllerFuture?.get()
                mediaController?.let { controller ->
                    setupControllerListener(controller)
                    syncControllerState(controller)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, MoreExecutors.directExecutor())
    }

    private fun setupControllerListener(controller: MediaController) {
        controller.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playerState.update { it.copy(isPlaying = isPlaying) }
                if (isPlaying) {
                    startProgressPolling()
                } else {
                    stopProgressPolling()
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val song = getSongFromMediaItem(mediaItem)
                _playerState.update { it.copy(currentSong = song, duration = controller.duration) }
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                _playerState.update { it.copy(shuffleModeEnabled = shuffleModeEnabled) }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                val domainRepeat = when (repeatMode) {
                    Player.REPEAT_MODE_ONE -> RepeatMode.ONE
                    Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                    else -> RepeatMode.NONE
                }
                _playerState.update { it.copy(repeatMode = domainRepeat) }
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                _playerState.update { it.copy(playbackSpeed = playbackParameters.speed) }
            }
        })
    }

    private fun syncControllerState(controller: MediaController) {
        val song = getSongFromMediaItem(controller.currentMediaItem)
        val domainRepeat = when (controller.repeatMode) {
            Player.REPEAT_MODE_ONE -> RepeatMode.ONE
            Player.REPEAT_MODE_ALL -> RepeatMode.ALL
            else -> RepeatMode.NONE
        }
        _playerState.update {
            it.copy(
                currentSong = song,
                isPlaying = controller.isPlaying,
                shuffleModeEnabled = controller.shuffleModeEnabled,
                repeatMode = domainRepeat,
                playbackSpeed = controller.playbackParameters.speed,
                playbackPosition = controller.currentPosition,
                duration = controller.duration
            )
        }
        if (controller.isPlaying) {
            startProgressPolling()
        }
    }

    private fun getSongFromMediaItem(mediaItem: MediaItem?): Song? {
        if (mediaItem == null) return null
        val meta = mediaItem.mediaMetadata
        return Song(
            id = mediaItem.mediaId.toLongOrNull() ?: 0,
            title = meta.title?.toString() ?: "Unknown",
            artist = meta.artist?.toString() ?: "Unknown",
            album = meta.albumTitle?.toString() ?: "Unknown",
            albumArtist = meta.albumArtist?.toString(),
            genre = meta.genre?.toString(),
            trackNumber = meta.trackNumber,
            duration = _playerState.value.duration,
            filePath = mediaItem.requestMetadata.mediaUri?.path ?: "",
            dateAdded = 0,
            dateModified = 0,
            albumArtUri = meta.artworkUri?.toString(),
            lyricsPath = null,
            bitrate = null,
            sampleRate = null,
            favorite = false
        )
    }

    @OptIn(UnstableApi::class)
    fun setQueue(songs: List<Song>, startIndex: Int = 0) {
        mediaController?.let { controller ->
            originalQueueList = songs
            val mediaItems = songs.map { song ->
                MediaItem.Builder()
                    .setMediaId(song.id.toString())
                    .setUri(Uri.parse(song.filePath))
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(song.title)
                            .setArtist(song.artist)
                            .setAlbumTitle(song.album)
                            .setArtworkUri(song.albumArtUri?.let { Uri.parse(it) })
                            .build()
                    )
                    .build()
            }
            controller.setMediaItems(mediaItems, startIndex, 0L)
            controller.prepare()
            controller.play()
            _playerState.update { it.copy(queue = songs) }
        }
    }

    fun play() {
        mediaController?.play()
    }

    fun pause() {
        mediaController?.pause()
    }

    fun stop() {
        mediaController?.stop()
    }

    fun seekTo(positionMs: Long) {
        mediaController?.seekTo(positionMs)
        _playerState.update { it.copy(playbackPosition = positionMs) }
    }

    fun skipToNext() {
        mediaController?.seekToNext()
    }

    fun skipToPrevious() {
        mediaController?.seekToPrevious()
    }

    fun toggleShuffle() {
        mediaController?.let { controller ->
            val enabled = !controller.shuffleModeEnabled
            controller.shuffleModeEnabled = enabled
        }
    }

    fun toggleRepeat() {
        mediaController?.let { controller ->
            val nextMode = when (controller.repeatMode) {
                Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                else -> Player.REPEAT_MODE_OFF
            }
            controller.repeatMode = nextMode
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        mediaController?.let { controller ->
            controller.setPlaybackSpeed(speed)
        }
    }

    fun setBassBoost(strength: Short) {
        equalizerController.setBassBoostStrength(strength)
        _playerState.update { it.copy(bassBoostStrength = strength) }
    }

    fun setVirtualizer(strength: Short) {
        equalizerController.setVirtualizerStrength(strength)
        _playerState.update { it.copy(virtualizerStrength = strength) }
    }

    private fun startProgressPolling() {
        progressJob?.cancel()
        progressJob = playerScope.launch {
            while (true) {
                mediaController?.let { controller ->
                    _playerState.update {
                        it.copy(
                            playbackPosition = controller.currentPosition,
                            duration = controller.duration
                        )
                    }
                }
                delay(500)
            }
        }
    }

    private fun stopProgressPolling() {
        progressJob?.cancel()
    }

    fun release() {
        playerScope.launch {
            controllerFuture?.let { MediaController.releaseFuture(it) }
            equalizerController.release()
        }
    }
}
