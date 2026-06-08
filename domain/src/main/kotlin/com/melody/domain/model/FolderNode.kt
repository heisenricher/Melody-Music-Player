package com.melody.domain.model

/**
 * Domain model representing a directory in the device storage.
 */
data class FolderNode(
    val name: String,
    val path: String,
    val subFoldersCount: Int,
    val songsCount: Int,
    val isPinned: Boolean,
    val isExcluded: Boolean
)
