package com.example.routemaster.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class SoundController {

    private val sampleRate = 44100
    private val scope = CoroutineScope(Dispatchers.Default)
    var isEnabled: Boolean = true

    enum class SoundEffect {
        TILE_SELECT,
        ROTATE,
        TILE_PLACED,
        ROUTE_EXTENDED,
        NEW_LONGEST_ROUTE,
        GAME_COMPLETE
    }

    // Pre-rendered PCM audio buffers for instant zero-latency playback
    private val audioBuffers: Map<SoundEffect, ShortArray> by lazy {
        mapOf(
            SoundEffect.TILE_SELECT to generateSelectTone(),
            SoundEffect.ROTATE to generateRotateTone(),
            SoundEffect.TILE_PLACED to generatePlacedTone(),
            SoundEffect.ROUTE_EXTENDED to generateExtendedTone(),
            SoundEffect.NEW_LONGEST_ROUTE to generateNewRecordTone(),
            SoundEffect.GAME_COMPLETE to generateVictoryTone()
        )
    }

    fun play(effect: SoundEffect) {
        if (!isEnabled) return
        val buffer = audioBuffers[effect] ?: return

        scope.launch {
            try {
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, buffer.size)
                track.play()

                // Small delay to allow playback to complete before releasing
                val durationMs = (buffer.size * 1000L / sampleRate) + 50
                kotlinx.coroutines.delay(durationMs)
                track.stop()
                track.release()
            } catch (_: Exception) {
                // Ignore audio playback exceptions if audio device is unavailable
            }
        }
    }

    private fun generateSelectTone(): ShortArray {
        val durationMs = 40
        val numSamples = (sampleRate * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        val freq = 880.0
        for (i in 0 until numSamples) {
            val env = 1.0 - (i.toDouble() / numSamples)
            val sample = sin(2.0 * Math.PI * freq * i / sampleRate) * env * 0.25
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateRotateTone(): ShortArray {
        val durationMs = 50
        val numSamples = (sampleRate * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val freq = 1200.0 - (progress * 400.0) // pitch drop chirp
            val env = 1.0 - progress
            val sample = sin(2.0 * Math.PI * freq * i / sampleRate) * env * 0.28
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generatePlacedTone(): ShortArray {
        val durationMs = 65
        val numSamples = (sampleRate * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        val freq = 320.0
        for (i in 0 until numSamples) {
            val env = (1.0 - (i.toDouble() / numSamples)).let { it * it }
            val sample = sin(2.0 * Math.PI * freq * i / sampleRate) * env * 0.35
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateExtendedTone(): ShortArray {
        val durationMs = 140
        val numSamples = (sampleRate * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        val freq = 659.25 // E5
        for (i in 0 until numSamples) {
            val env = 1.0 - (i.toDouble() / numSamples)
            val sample = (sin(2.0 * Math.PI * freq * i / sampleRate) +
                    0.3 * sin(2.0 * Math.PI * (freq * 2) * i / sampleRate)) * env * 0.25
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateNewRecordTone(): ShortArray {
        val durationMs = 240
        val numSamples = (sampleRate * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        val half = numSamples / 2
        for (i in 0 until numSamples) {
            val freq = if (i < half) 523.25 else 783.99 // C5 -> G5
            val env = if (i < half) {
                1.0 - (i.toDouble() / half) * 0.4
            } else {
                1.0 - ((i - half).toDouble() / (numSamples - half))
            }
            val sample = sin(2.0 * Math.PI * freq * i / sampleRate) * env * 0.3
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateVictoryTone(): ShortArray {
        val durationMs = 450
        val numSamples = (sampleRate * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        val c5 = 523.25
        val e5 = 659.25
        val g5 = 783.99
        val c6 = 1046.50
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val env = (1.0 - progress) * 0.22
            val sample = (sin(2.0 * Math.PI * c5 * i / sampleRate) * 0.4 +
                    sin(2.0 * Math.PI * e5 * i / sampleRate) * 0.3 +
                    sin(2.0 * Math.PI * g5 * i / sampleRate) * 0.3 +
                    sin(2.0 * Math.PI * c6 * i / sampleRate) * 0.2) * env
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }
}
