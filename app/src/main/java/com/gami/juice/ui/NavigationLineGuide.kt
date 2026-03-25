package com.gami.juice.ui

import kotlin.math.sqrt

/**
 * Produces world-space navigation line points on road surface toward nearest destination.
 */
class NavigationLineGuide {

    enum class DestinationType { FUEL_STATION, GARAGE }

    data class Vec3(val x: Float, val y: Float, val z: Float)
    data class Destination(val type: DestinationType, val position: Vec3)

    data class LineSegment(
        val start: Vec3,
        val end: Vec3,
        val intensity: Float
    )

    fun buildLine(
        carPosition: Vec3,
        roadNormal: Vec3,
        destinations: List<Destination>,
        maxSegments: Int = 24
    ): List<LineSegment> {
        val target = destinations.minByOrNull { distance(carPosition, it.position) } ?: return emptyList()
        val delta = Vec3(target.position.x - carPosition.x, target.position.y - carPosition.y, target.position.z - carPosition.z)

        val segments = mutableListOf<LineSegment>()
        for (i in 0 until maxSegments) {
            val t0 = i / maxSegments.toFloat()
            val t1 = (i + 1) / maxSegments.toFloat()
            val p0 = onRoad(carPosition, delta, t0, roadNormal)
            val p1 = onRoad(carPosition, delta, t1, roadNormal)
            val pulse = 0.65f + (i / maxSegments.toFloat()) * 0.35f
            segments += LineSegment(p0, p1, pulse)
        }
        return segments
    }

    private fun onRoad(origin: Vec3, delta: Vec3, t: Float, roadNormal: Vec3): Vec3 {
        val x = origin.x + delta.x * t
        val y = origin.y + delta.y * t
        val z = origin.z + delta.z * t
        val nudge = 0.06f
        return Vec3(x + roadNormal.x * nudge, y + roadNormal.y * nudge, z + roadNormal.z * nudge)
    }

    private fun distance(a: Vec3, b: Vec3): Float {
        val dx = a.x - b.x
        val dy = a.y - b.y
        val dz = a.z - b.z
        return sqrt(dx * dx + dy * dy + dz * dz)
    }
}
