package com.gami.juice

class BiomeBlendSystem {

    enum class Biome { DESERT, RAINFOREST, RURAL }

    data class Color(val r: Float, val g: Float, val b: Float)

    data class BiomeVisuals(
        val fogColor: Color,
        val ambientLight: Color,
        val skyboxId: String
    )

    data class BlendOutput(
        val fogColor: Color,
        val ambientLight: Color,
        val skyboxA: String,
        val skyboxB: String,
        val skyboxBlend: Float
    )

    private val biomeVisualPresets = mapOf(
        Biome.DESERT to BiomeVisuals(Color(0.88f, 0.71f, 0.45f), Color(0.93f, 0.81f, 0.61f), "sky_desert"),
        Biome.RAINFOREST to BiomeVisuals(Color(0.28f, 0.49f, 0.42f), Color(0.47f, 0.73f, 0.57f), "sky_rainforest"),
        Biome.RURAL to BiomeVisuals(Color(0.61f, 0.73f, 0.81f), Color(0.78f, 0.84f, 0.92f), "sky_rural")
    )

    fun blend(from: Biome, to: Biome, distanceIntoTransition: Float): BlendOutput {
        val transitionDistance = 500f
        val t = (distanceIntoTransition / transitionDistance).coerceIn(0f, 1f)
        val smooth = t * t * (3f - 2f * t)
        val a = biomeVisualPresets.getValue(from)
        val b = biomeVisualPresets.getValue(to)
        return BlendOutput(
            fogColor = lerp(a.fogColor, b.fogColor, smooth),
            ambientLight = lerp(a.ambientLight, b.ambientLight, smooth),
            skyboxA = a.skyboxId,
            skyboxB = b.skyboxId,
            skyboxBlend = smooth
        )
    }

    private fun lerp(a: Color, b: Color, t: Float): Color = Color(
        r = a.r + (b.r - a.r) * t,
        g = a.g + (b.g - a.g) * t,
        b = a.b + (b.b - a.b) * t
    )
}
