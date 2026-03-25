package com.gami.juice.audio

/**
 * Crossfades tire loop between asphalt hum and off-road crunch.
 */
class SurfaceAudioFeedback(private val sink: SurfaceAudioSink) {

    enum class SurfaceType { ASPHALT, GRAVEL, SAND }

    interface SurfaceAudioSink {
        fun setAsphaltHum(volume: Float)
        fun setOffroadCrunch(volume: Float, pitch: Float)
    }

    fun update(surface: SurfaceType, speedMetersPerSecond: Float) {
        val speed01 = (speedMetersPerSecond / 55f).coerceIn(0f, 1f)

        when (surface) {
            SurfaceType.ASPHALT -> {
                sink.setAsphaltHum(volume = 0.2f + speed01 * 0.55f)
                sink.setOffroadCrunch(volume = 0f, pitch = 1f)
            }

            SurfaceType.GRAVEL -> {
                sink.setAsphaltHum(volume = 0.08f)
                sink.setOffroadCrunch(volume = 0.24f + speed01 * 0.68f, pitch = 0.95f + speed01 * 0.18f)
            }

            SurfaceType.SAND -> {
                sink.setAsphaltHum(volume = 0.03f)
                sink.setOffroadCrunch(volume = 0.2f + speed01 * 0.72f, pitch = 0.82f + speed01 * 0.12f)
            }
        }
    }
}
