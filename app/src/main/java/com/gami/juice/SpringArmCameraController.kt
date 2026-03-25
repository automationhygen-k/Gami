package com.gami.juice

import kotlin.math.max
import kotlin.math.min

/**
 * Third-person spring-arm camera controller with speed-based FOV and inertial lag.
 *
 * Engine-agnostic update contract:
 * - call [update] every frame with current car transform and speed.
 * - read [cameraState] and apply to your renderer camera.
 */
class SpringArmCameraController(
    private val config: Config = Config()
) {
    data class Config(
        val baseFovDeg: Float = 64f,
        val maxFovDeg: Float = 86f,
        val fovSpeedForMax: Float = 68f,
        val armLength: Float = 8.5f,
        val armHeight: Float = 2.8f,
        val lagStrength: Float = 8.0f,
        val lookAheadScale: Float = 0.05f
    )

    data class Vec3(val x: Float, val y: Float, val z: Float) {
        operator fun plus(other: Vec3) = Vec3(x + other.x, y + other.y, z + other.z)
        operator fun minus(other: Vec3) = Vec3(x - other.x, y - other.y, z - other.z)
        operator fun times(s: Float) = Vec3(x * s, y * s, z * s)
    }

    data class CarState(
        val position: Vec3,
        val forward: Vec3,
        val up: Vec3,
        val speedMetersPerSecond: Float
    )

    data class CameraState(
        val position: Vec3,
        val lookAt: Vec3,
        val fovDeg: Float
    )

    var cameraState: CameraState = CameraState(
        position = Vec3(0f, 4f, -8f),
        lookAt = Vec3(0f, 1f, 0f),
        fovDeg = config.baseFovDeg
    )
        private set

    fun update(deltaSeconds: Float, car: CarState) {
        val speedT = smooth01(car.speedMetersPerSecond / config.fovSpeedForMax)
        val targetFov = lerp(config.baseFovDeg, config.maxFovDeg, speedT)

        val lookAhead = car.forward * (car.speedMetersPerSecond * config.lookAheadScale)
        val targetLookAt = car.position + car.up * 1.4f + lookAhead
        val targetCamPos = car.position - car.forward * config.armLength + car.up * config.armHeight

        val alpha = 1f - kotlin.math.exp(-config.lagStrength * deltaSeconds)
        val blendedPos = lerp(cameraState.position, targetCamPos, alpha)
        val blendedLook = lerp(cameraState.lookAt, targetLookAt, alpha)
        val blendedFov = lerp(cameraState.fovDeg, targetFov, alpha)

        cameraState = CameraState(blendedPos, blendedLook, blendedFov)
    }

    private fun smooth01(v: Float): Float {
        val t = min(1f, max(0f, v))
        return t * t * (3f - 2f * t)
    }

    private fun lerp(a: Float, b: Float, t: Float): Float = a + (b - a) * t
    private fun lerp(a: Vec3, b: Vec3, t: Float): Vec3 =
        Vec3(lerp(a.x, b.x, t), lerp(a.y, b.y, t), lerp(a.z, b.z, t))
}
