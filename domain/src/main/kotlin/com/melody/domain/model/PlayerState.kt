package com.melody.domain.model

data class PlayerState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val playbackPosition: Long = 0L,
    val duration: Long = 0L,
    val shuffleModeEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.NONE,
    val queue: List<Song> = emptyList(),
    val playbackSpeed: Float = 1.0f,
    val equalizerEnabled: Boolean = false,
    val bassBoostStrength: Short = 0,
    val virtualizerStrength: Short = 0
)
