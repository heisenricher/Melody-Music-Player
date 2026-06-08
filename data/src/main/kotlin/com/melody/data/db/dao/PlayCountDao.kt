package com.melody.data.db.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface PlayCountDao {
    @Query("""
        INSERT INTO play_counts (songId, count) VALUES (:songId, 1)
        ON CONFLICT(songId) DO UPDATE SET count = count + 1
    """)
    suspend fun incrementPlayCount(songId: Long)
}
