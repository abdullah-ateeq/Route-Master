package com.example.routemaster.audio

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class HapticController(context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var isEnabled: Boolean = true

    enum class HapticType {
        SELECTION,
        ROTATION,
        PLACEMENT,
        NEW_LONGEST_ROUTE,
        GAME_COMPLETE
    }

    fun trigger(type: HapticType) {
        if (!isEnabled || vibrator == null || !vibrator.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                when (type) {
                    HapticType.SELECTION -> {
                        // Very light tick
                        vibrator.vibrate(VibrationEffect.createOneShot(14, VibrationEffect.DEFAULT_AMPLITUDE))
                    }
                    HapticType.ROTATION -> {
                        // Very light tick
                        vibrator.vibrate(VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE))
                    }
                    HapticType.PLACEMENT -> {
                        // Light solid click
                        vibrator.vibrate(VibrationEffect.createOneShot(28, VibrationEffect.DEFAULT_AMPLITUDE))
                    }
                    HapticType.NEW_LONGEST_ROUTE -> {
                        // Medium-light pulse sequence: 2 pulses
                        val timings = longArrayOf(0, 20, 40, 35)
                        val amplitudes = intArrayOf(0, 160, 0, 220)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && vibrator.hasAmplitudeControl()) {
                            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                        } else {
                            vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
                        }
                    }
                    HapticType.GAME_COMPLETE -> {
                        // Distinct success feedback
                        val timings = longArrayOf(0, 35, 50, 60)
                        vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                when (type) {
                    HapticType.SELECTION, HapticType.ROTATION -> vibrator.vibrate(12)
                    HapticType.PLACEMENT -> vibrator.vibrate(28)
                    HapticType.NEW_LONGEST_ROUTE -> vibrator.vibrate(longArrayOf(0, 20, 40, 35), -1)
                    HapticType.GAME_COMPLETE -> vibrator.vibrate(longArrayOf(0, 35, 50, 60), -1)
                }
            }
        } catch (_: Exception) {
            // Silently ignore vibration errors
        }
    }
}
