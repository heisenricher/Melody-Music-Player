package com.melody.domain.model

/**
 * Domain model representing a music artist.
 */
data class Artist(
    val id: Long,
    val name: String,
    val artwork: String?
)
