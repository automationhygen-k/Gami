package com.gami.juice

/**
 * Cinematic intro: start high above, dive to cockpit, then hand off to gameplay camera.
 */
class AirdropIntroSequence {

    data class Vec3(val x: Float, val y: Float, val z: Float) {
        operator fun plus(o: Vec3) = Vec3(x + o.x, y + o.y, z + o.z)
        operator fun minus(o: Vec3) = Vec3(x - o.x, y - o.y, z - o.z)
        operator fun times(t: Float) = Vec3(x * t, y * t, z * t)
    }

    data class Pose(val position: Vec3, val lookAt: Vec3, val fovDeg: Float)

    private val durationSeconds = 4.2f

    fun evaluate(carPosition: Vec3, carForward: Vec3, elapsedSeconds: Float): Pair<Pose, Boolean> {
        val t = (elapsedSeconds / durationSeconds).coerceIn(0f, 1f)
        val ease = 1f - (1f - t) * (1f - t) * (1f - t)

        val startPos = carPosition + Vec3(0f, 85f, -20f)
        val endPos = carPosition + carForward * 0.35f + Vec3(0f, 1.15f, 0f)

        val startLook = carPosition
        val endLook = carPosition + carForward * 18f

        val pose = Pose(
            position = lerp(startPos, endPos, ease),
            lookAt = lerp(startLook, endLook, ease),
            fovDeg = lerp(52f, 70f, ease)
        )

        return pose to (t >= 1f)
    }

    private fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t
    private fun lerp(a: Vec3, b: Vec3, t: Float) = Vec3(
        lerp(a.x, b.x, t),
        lerp(a.y, b.y, t),
        lerp(a.z, b.z, t)
    )
}
