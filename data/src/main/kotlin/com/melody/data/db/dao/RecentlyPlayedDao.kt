package com.melody.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.melody.data.db.entity.RecentlyPlayedEntity

@Dao
interface RecentlyPlayedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentlyPlayed(recentlyPlayed: RecentlyPlayedEntity)
}
