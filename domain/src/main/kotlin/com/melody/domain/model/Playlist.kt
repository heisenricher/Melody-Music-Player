package com.melody.domain.model

/**
 * Domain model representing a music playlist.
 */
data class Playlist(
    val id: Long,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long
)
