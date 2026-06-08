package com.melody.data.di

import android.content.Context
import androidx.room.Room
import com.melody.data.db.MelodyDatabase
import com.melody.data.db.dao.SongDao
import com.melody.data.db.dao.PlaylistDao
import com.melody.data.db.dao.RecentlyPlayedDao
import com.melody.data.db.dao.PlayCountDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MelodyDatabase {
        return Room.databaseBuilder(
            context,
            MelodyDatabase::class.java,
            "melody.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideSongDao(db: MelodyDatabase): SongDao = db.songDao()

    @Provides
    fun providePlaylistDao(db: MelodyDatabase): PlaylistDao = db.playlistDao()

    @Provides
    fun provideRecentlyPlayedDao(db: MelodyDatabase): RecentlyPlayedDao = db.recentlyPlayedDao()

    @Provides
    fun providePlayCountDao(db: MelodyDatabase): PlayCountDao = db.playCountDao()
}
