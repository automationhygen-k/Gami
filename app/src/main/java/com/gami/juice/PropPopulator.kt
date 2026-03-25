package com.gami.juice

import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Ambient prop generation using Bridson Poisson Disk Sampling.
 */
class PropPopulator {

    enum class Biome { DESERT, RAINFOREST, RURAL }
    enum class PropType { CACTUS, TREE, HUT }

    data class Vec2(val x: Float, val y: Float)
    data class PropInstance(val position: Vec2, val type: PropType, val yawDegrees: Float, val scale: Float)

    fun populate(
        biome: Biome,
        minX: Float,
        maxX: Float,
        minY: Float,
        maxY: Float,
        seed: Int,
        minDistance: Float = 8f
    ): List<PropInstance> {
        val points = poissonDisk(minX, maxX, minY, maxY, minDistance, 30, Random(seed))
        val propType = when (biome) {
            Biome.DESERT -> PropType.CACTUS
            Biome.RAINFOREST -> PropType.TREE
            Biome.RURAL -> PropType.HUT
        }
        return points.mapIndexed { i, p ->
            val rng = Random(seed + i * 31)
            PropInstance(
                position = p,
                type = propType,
                yawDegrees = rng.nextFloat() * 360f,
                scale = 0.85f + rng.nextFloat() * 0.45f
            )
        }
    }

    private fun poissonDisk(
        minX: Float,
        maxX: Float,
        minY: Float,
        maxY: Float,
        radius: Float,
        k: Int,
        random: Random
    ): List<Vec2> {
        val cellSize = radius / sqrt(2f)
        val gridW = ((maxX - minX) / cellSize).toInt() + 1
        val gridH = ((maxY - minY) / cellSize).toInt() + 1
        val grid = Array(gridW * gridH) { -1 }
        val points = mutableListOf<Vec2>()
        val active = mutableListOf<Int>()

        fun gridIndex(p: Vec2): Int {
            val gx = floor((p.x - minX) / cellSize).toInt().coerceIn(0, gridW - 1)
            val gy = floor((p.y - minY) / cellSize).toInt().coerceIn(0, gridH - 1)
            return gy * gridW + gx
        }

        fun inBounds(p: Vec2) = p.x in minX..maxX && p.y in minY..maxY

        fun farEnough(p: Vec2): Boolean {
            val gx = floor((p.x - minX) / cellSize).toInt()
            val gy = floor((p.y - minY) / cellSize).toInt()
            for (y in (gy - 2)..(gy + 2)) {
                if (y !in 0 until gridH) continue
                for (x in (gx - 2)..(gx + 2)) {
                    if (x !in 0 until gridW) continue
                    val idx = grid[y * gridW + x]
                    if (idx == -1) continue
                    val q = points[idx]
                    val dx = p.x - q.x
                    val dy = p.y - q.y
                    if (dx * dx + dy * dy < radius * radius) return false
                }
            }
            return true
        }

        val initial = Vec2(
            x = random.nextFloat() * (maxX - minX) + minX,
            y = random.nextFloat() * (maxY - minY) + minY
        )
        points += initial
        active += 0
        grid[gridIndex(initial)] = 0

        while (active.isNotEmpty()) {
            val activeIdx = random.nextInt(active.size)
            val pointIndex = active[activeIdx]
            val center = points[pointIndex]
            var found = false

            repeat(k) {
                val angle = random.nextFloat() * (Math.PI * 2f).toFloat()
                val dist = radius * (1f + random.nextFloat())
                val candidate = Vec2(
                    center.x + cos(angle) * dist,
                    center.y + sin(angle) * dist
                )
                if (inBounds(candidate) && farEnough(candidate)) {
                    points += candidate
                    val newIndex = points.lastIndex
                    active += newIndex
                    grid[gridIndex(candidate)] = newIndex
                    found = true
                    return@repeat
                }
            }

            if (!found) active.removeAt(activeIdx)
        }

        return points
    }
}
