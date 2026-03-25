package com.gami.juice.audio

import kotlin.math.sqrt

/**
 * Controls spatial biome ambience.
 * - Rainforest: directional rain patter 3D emitter
 * - Desert: wind whistle scales with speed
 */
class EnvironmentalAudioController(private val sink: SpatialAmbientSink) {

    enum class Biome { DESERT, RAINFOREST, RURAL }

    data class Vec3(val x: Float, val y: Float, val z: Float) {
        operator fun minus(other: Vec3) = Vec3(x - other.x, y - other.y, z - other.z)
    }

    interface SpatialAmbientSink {
        fun setRainEmitter(position: Vec3, volume: Float)
        fun setDesertWind(volume: Float, pitch: Float)
    }

    fun update(
        biome: Biome,
        speedMetersPerSecond: Float,
        listenerPosition: Vec3,
        rainCellPosition: Vec3
    ) {
        val speed01 = (speedMetersPerSecond / 65f).coerceIn(0f, 1f)

        val rainDistance = distance(listenerPosition, rainCellPosition)
        val rainAttenuation = (1f - (rainDistance / 80f)).coerceIn(0f, 1f)

        if (biome == Biome.RAINFOREST) {
            sink.setRainEmitter(rainCellPosition, 0.2f + rainAttenuation * 0.8f)
            sink.setDesertWind(volume = 0f, pitch = 0.9f)
        } else if (biome == Biome.DESERT) {
            sink.setRainEmitter(rainCellPosition, 0f)
            sink.setDesertWind(
                volume = 0.2f + speed01 * 0.85f,
                pitch = 0.8f + speed01 * 0.35f
            )
        } else {
            sink.setRainEmitter(rainCellPosition, 0f)
            sink.setDesertWind(volume = 0.08f, pitch = 0.92f)
        }
    }

    private fun distance(a: Vec3, b: Vec3): Float {
        val d = a - b
        return sqrt(d.x * d.x + d.y * d.y + d.z * d.z)
    }
}
