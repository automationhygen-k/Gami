package com.gami.juice

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.sin
import kotlin.random.Random

/**
 * Minimal glass-morphic HUD speedometer with redline shake.
 */
class DynamicHudView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val platePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(96, 255, 255, 255)
        style = Paint.Style.FILL
    }

    private val rimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(180, 220, 240, 255)
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 54f
        isFakeBoldText = true
    }

    var speedKph: Int = 0
    var redlineThresholdKph: Int = 190

    private var elapsed = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        elapsed += 1f / 60f

        val w = width.toFloat()
        val h = height.toFloat()
        val plate = RectF(w * 0.32f, h * 0.10f, w * 0.68f, h * 0.27f)

        val redline = speedKph >= redlineThresholdKph
        val shake = if (redline) 2.2f else 0f
        val jitterX = if (redline) (sin(elapsed * 68f) * shake + Random.nextFloat() * 1.2f) else 0f
        val jitterY = if (redline) (sin(elapsed * 41f) * shake + Random.nextFloat() * 1.2f) else 0f

        canvas.save()
        canvas.translate(jitterX, jitterY)
        canvas.drawRoundRect(plate, 24f, 24f, platePaint)
        canvas.drawRoundRect(plate, 24f, 24f, rimPaint)

        val speedLabel = "$speedKph"
        val unitLabel = "km/h"
        canvas.drawText(speedLabel, w * 0.50f, h * 0.20f, textPaint)

        textPaint.textSize = 22f
        textPaint.alpha = 190
        canvas.drawText(unitLabel, w * 0.50f, h * 0.245f, textPaint)
        textPaint.textSize = 54f
        textPaint.alpha = 255

        canvas.restore()

        postInvalidateOnAnimation()
    }
}
