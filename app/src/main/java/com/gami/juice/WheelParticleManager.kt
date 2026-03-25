package com.gami.juice

/**
 * Selects wheel particle effect presets based on biome and velocity.
 * Rendering backend should bind these outputs to actual particle emitters.
 */
class WheelParticleManager {

    enum class Biome { DESERT, RAINFOREST, RURAL }
    enum class ParticleType { NONE, DUST, WATER_SPRAY }

    data class WheelParticleState(
        val particleType: ParticleType,
        val emissionRate: Float,
        val initialVelocity: Float,
        val opacity: Float
    )

    fun evaluate(biome: Biome, speedMetersPerSecond: Float): WheelParticleState {
        val speed01 = (speedMetersPerSecond / 60f).coerceIn(0f, 1f)
        return when (biome) {
            Biome.DESERT -> WheelParticleState(
                particleType = ParticleType.DUST,
                emissionRate = 8f + speed01 * 55f,
                initialVelocity = 1.2f + speed01 * 8f,
                opacity = 0.3f + speed01 * 0.5f
            )

            Biome.RAINFOREST -> WheelParticleState(
                particleType = ParticleType.WATER_SPRAY,
                emissionRate = 12f + speed01 * 72f,
                initialVelocity = 1.5f + speed01 * 10f,
                opacity = 0.45f + speed01 * 0.45f
            )

            Biome.RURAL -> WheelParticleState(
                particleType = ParticleType.NONE,
                emissionRate = 0f,
                initialVelocity = 0f,
                opacity = 0f
            )
        }
    }
}
