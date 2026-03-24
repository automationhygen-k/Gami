package com.gami.juice.feedback

/**
 * Controls tail-light streak visibility for low-light emphasis of speed.
 */
class TailLightStreakController {

    data class StreakState(
        val enabled: Boolean,
        val bloomIntensity: Float,
        val ghostLength: Float,
        val opacity: Float
    )

    fun evaluate(isNight: Boolean, inLowVisibilityRainforest: Boolean, speedKph: Float): StreakState {
        val visibilityTrigger = isNight || inLowVisibilityRainforest
        val speed01 = (speedKph / 220f).coerceIn(0f, 1f)

        val opacity = if (visibilityTrigger) (0.2f + speed01 * 0.8f) else 0f
        return StreakState(
            enabled = opacity > 0.01f,
            bloomIntensity = 0.6f + speed01 * 1.5f,
            ghostLength = 0.12f + speed01 * 0.45f,
            opacity = opacity
        )
    }
}
