package com.melody.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.melody.data.db.dao.SongDao
import com.melody.data.db.dao.PlaylistDao
import com.melody.data.db.dao.RecentlyPlayedDao
import com.melody.data.db.dao.PlayCountDao
import com.melody.data.db.entity.SongEntity
import com.melody.data.db.entity.PlaylistEntity
import com.melody.data.db.entity.PlaylistSongEntity
import com.melody.data.db.entity.RecentlyPlayedEntity
import com.melody.data.db.entity.PlayCountEntity

@Database(
    entities = [
        SongEntity::class,
        PlaylistEntity::class,
        PlaylistSongEntity::class,
        RecentlyPlayedEntity::class,
        PlayCountEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MelodyDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun recentlyPlayedDao(): RecentlyPlayedDao
    abstract fun playCountDao(): PlayCountDao
}
