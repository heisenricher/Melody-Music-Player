package com.melody.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.melody.domain.model.Playlist

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomain(): Playlist = Playlist(
        id = id,
        name = name,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(playlist: Playlist): PlaylistEntity = PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            createdAt = playlist.createdAt,
            updatedAt = playlist.updatedAt
        )
    }
}
