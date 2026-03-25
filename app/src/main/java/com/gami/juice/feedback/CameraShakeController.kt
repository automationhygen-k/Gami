package com.gami.juice.feedback

import kotlin.math.floor

/**
 * Perlin-noise-like procedural shake for high speed and off-road driving.
 */
class CameraShakeController(
    private val noise: ValueNoise = ValueNoise(seed = 1337)
) {
    data class Vec3(val x: Float, val y: Float, val z: Float)

    data class ShakeOutput(val offset: Vec3, val rotationalJitterDeg: Vec3)

    fun evaluate(timeSeconds: Float, speedKph: Float, offRoad: Boolean): ShakeOutput {
        val highSpeedAmount = ((speedKph - 150f) / 110f).coerceIn(0f, 1f)
        val offRoadAmount = if (offRoad) 0.65f else 0f
        val intensity = (highSpeedAmount + offRoadAmount).coerceIn(0f, 1f)

        val frequency = 5f + intensity * 12f
        val tx = noise.noise1D(timeSeconds * frequency)
        val ty = noise.noise1D((timeSeconds + 15.3f) * frequency)
        val tz = noise.noise1D((timeSeconds + 37.1f) * frequency)

        return ShakeOutput(
            offset = Vec3(tx * 0.06f * intensity, ty * 0.04f * intensity, tz * 0.08f * intensity),
            rotationalJitterDeg = Vec3(tx * 0.75f * intensity, ty * 0.95f * intensity, tz * 0.6f * intensity)
        )
    }

    class ValueNoise(private val seed: Int) {
        fun noise1D(x: Float): Float {
            val x0 = floor(x).toInt()
            val x1 = x0 + 1
            val t = x - x0
            val fade = t * t * (3f - 2f * t)
            return lerp(hashToUnit(x0), hashToUnit(x1), fade)
        }

        private fun hashToUnit(i: Int): Float {
            var h = i * 374761393 + seed * 668265263
            h = (h xor (h shr 13)) * 1274126177
            h = h xor (h shr 16)
            return ((h and 0x7fffffff) / 1073741824f) - 1f
        }

        private fun lerp(a: Float, b: Float, t: Float): Float = a + (b - a) * t
    }
}
