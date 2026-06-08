package com.melody.player.equalizer

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EqualizerController @Inject constructor() {

    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null

    fun initEffects(audioSessionId: Int) {
        if (audioSessionId == 0) return
        try {
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = true
            }
            bassBoost = BassBoost(0, audioSessionId).apply {
                enabled = true
            }
            virtualizer = Virtualizer(0, audioSessionId).apply {
                enabled = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setEqualizerBandLevel(band: Short, level: Short) {
        try {
            equalizer?.setBandLevel(band, level)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getEqualizerBandsCount(): Short {
        return equalizer?.numberOfBands ?: 0
    }

    fun getBandFrequency(band: Short): Int {
        return equalizer?.getCenterFreq(band) ?: 0
    }

    fun getBandLevelRange(): ShortArray {
        return equalizer?.bandLevelRange ?: shortArrayOf(-1500, 1500)
    }

    fun setBassBoostStrength(strength: Short) {
        try {
            bassBoost?.setStrength(strength)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setVirtualizerStrength(strength: Short) {
        try {
            virtualizer?.setStrength(strength)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        equalizer?.release()
        bassBoost?.release()
        virtualizer?.release()
        equalizer = null
        bassBoost = null
        virtualizer = null
    }
}
