package com.melody.player.di

import android.content.Context
import com.melody.player.controller.MelodyPlayer
import com.melody.player.equalizer.EqualizerController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {

    @Provides
    @Singleton
    fun provideEqualizerController(): EqualizerController {
        return EqualizerController()
    }

    @Provides
    @Singleton
    fun provideMelodyPlayer(
        @ApplicationContext context: Context,
        equalizerController: EqualizerController
    ): MelodyPlayer {
        return MelodyPlayer(context, equalizerController)
    }
}
