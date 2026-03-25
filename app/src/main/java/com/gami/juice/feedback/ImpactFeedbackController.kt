package com.gami.juice.feedback

/**
 * Hit-stop + radial shake payload when obstacles are hit.
 */
class ImpactFeedbackController {

    data class ImpactEvent(
        val collisionSpeedKph: Float,
        val normalX: Float,
        val normalY: Float
    )

    data class ImpactResponse(
        val freezeMillis: Long,
        val radialShakeAmplitude: Float,
        val radialShakeDurationSeconds: Float
    )

    fun onObstacleHit(event: ImpactEvent): ImpactResponse {
        val intensity = (event.collisionSpeedKph / 180f).coerceIn(0.15f, 1f)
        return ImpactResponse(
            freezeMillis = 50L,
            radialShakeAmplitude = 0.25f + intensity * 1.1f,
            radialShakeDurationSeconds = 0.14f + intensity * 0.12f
        )
    }
}
