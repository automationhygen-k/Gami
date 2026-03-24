package com.gami.juice.audio

import kotlin.math.abs

/**
 * Dynamic engine loop controller:
 * - pitch/volume track RPM and gear load
 * - emits gear-shift pop events for one-shot playback
 */
class SoundManager(
    private val sink: EngineAudioSink,
    private val config: Config = Config()
) {
    interface EngineAudioSink {
        fun setLoopPitch(pitch: Float)
        fun setLoopVolume(volume: Float)
        fun playShiftPop(intensity: Float)
    }

    data class Config(
        val idleRpm: Float = 900f,
        val redlineRpm: Float = 8200f,
        val minPitch: Float = 0.72f,
        val maxPitch: Float = 1.52f,
        val minVolume: Float = 0.25f,
        val maxVolume: Float = 1.00f
    )

    private var previousGear: Int = 1

    fun update(rpm: Float, gear: Int, throttle01: Float, deltaSeconds: Float) {
        val normalizedRpm = ((rpm - config.idleRpm) / (config.redlineRpm - config.idleRpm)).coerceIn(0f, 1f)
        val throttle = throttle01.coerceIn(0f, 1f)

        val growlBoost = smoothStep(0.6f, 1f, normalizedRpm) * 0.2f
        val targetPitch = lerp(config.minPitch, config.maxPitch, normalizedRpm) + growlBoost
        val targetVolume = lerp(config.minVolume, config.maxVolume, normalizedRpm * 0.7f + throttle * 0.3f)

        sink.setLoopPitch(targetPitch)
        sink.setLoopVolume(targetVolume)

        if (gear != previousGear) {
            val shiftIntensity = (abs(gear - previousGear) / 2f).coerceIn(0.4f, 1f)
            sink.playShiftPop(shiftIntensity)
            previousGear = gear
        }

        // deltaSeconds kept for future attack/release smoothing support.
        @Suppress("UNUSED_VARIABLE")
        val frameDt = deltaSeconds
    }

    private fun lerp(a: Float, b: Float, t: Float): Float = a + (b - a) * t

    private fun smoothStep(edge0: Float, edge1: Float, x: Float): Float {
        val t = ((x - edge0) / (edge1 - edge0)).coerceIn(0f, 1f)
        return t * t * (3f - 2f * t)
    }
}
