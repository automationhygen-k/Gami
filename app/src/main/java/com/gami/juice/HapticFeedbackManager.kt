package com.gami.juice

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Game-feel haptics:
 * - Gear shift: short thump pulse.
 * - Off-road: low amplitude rumble while active.
 */
class HapticFeedbackManager(context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private var offroadRumbleActive = false

    fun onGearShift() {
        val v = vibrator ?: return
        if (!v.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(35L, 175))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(35L)
        }
    }

    fun setOffroadRumble(enabled: Boolean) {
        if (enabled == offroadRumbleActive) return
        offroadRumbleActive = enabled

        val v = vibrator ?: return
        if (!v.hasVibrator()) return

        if (enabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(longArrayOf(0, 80, 40), intArrayOf(0, 48, 0), 0)
                v.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(longArrayOf(0, 80, 40), 0)
            }
        } else {
            v.cancel()
        }
    }
}
