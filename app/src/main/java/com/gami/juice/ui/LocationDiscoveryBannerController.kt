package com.gami.juice.ui

/**
 * Horizon-style location discovery banner model.
 */
class LocationDiscoveryBannerController {

    data class BannerState(
        val visible: Boolean,
        val label: String,
        val alpha: Float,
        val scale: Float
    )

    private var activeLabel: String = ""
    private var timer = 0f
    private val holdSeconds = 1.8f
    private val fadeSeconds = 0.8f

    fun triggerDiscovery(locationName: String) {
        activeLabel = "$locationName Discovered"
        timer = 0f
    }

    fun update(deltaSeconds: Float): BannerState {
        timer += deltaSeconds
        val total = holdSeconds + fadeSeconds
        if (activeLabel.isEmpty() || timer >= total) {
            if (timer >= total) activeLabel = ""
            return BannerState(false, "", 0f, 0.98f)
        }

        val alpha = if (timer <= holdSeconds) 1f else 1f - ((timer - holdSeconds) / fadeSeconds).coerceIn(0f, 1f)
        val scale = 1f + (1f - alpha) * 0.03f
        return BannerState(true, activeLabel, alpha, scale)
    }
}
