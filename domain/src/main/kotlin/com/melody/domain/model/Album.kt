package com.melody.domain.model

/**
 * Domain model representing a music album.
 */
data class Album(
    val id: Long,
    val name: String,
    val artist: String,
    val artwork: String?
)
