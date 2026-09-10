package com.example.puzzlerush.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class SoundSynthesizer {

    private val scope = CoroutineScope(Dispatchers.Default)
    var isEnabled: Boolean = true

    private fun playTone(
        durationMs: Int,
        sampleRate: Int = 22050,
        frequencyProducer: (Float) -> Float,
        amplitudeEnvelope: (Float) -> Float
    ) {
        if (!isEnabled) return

        scope.launch {
            try {
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                var phase = 0.0
                for (i in 0 until numSamples) {
                    val progress = i.toFloat() / numSamples
                    val freq = frequencyProducer(progress)
                    val env = amplitudeEnvelope(progress)

                    phase += 2.0 * PI * freq / sampleRate
                    val sample = (sin(phase) * Short.MAX_VALUE * 0.45 * env).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, buffer.size)
                track.play()
                // Auto-release after playing
                scope.launch {
                    kotlinx.coroutines.delay(durationMs.toLong() + 100)
                    try {
                        track.stop()
                        track.release()
                    } catch (_: Exception) {}
                }
            } catch (_: Exception) {
                // Audio failure safe fallback
            }
        }
    }

    fun playTap() {
        playTone(
            durationMs = 40,
            frequencyProducer = { 650f },
            amplitudeEnvelope = { 1f - it }
        )
    }

    fun playWhoosh() {
        playTone(
            durationMs = 90,
            frequencyProducer = { progress -> 550f + progress * 650f },
            amplitudeEnvelope = { progress ->
                if (progress < 0.2f) progress / 0.2f else 1f - (progress - 0.2f) / 0.8f
            }
        )
    }

    fun playBump() {
        playTone(
            durationMs = 95,
            frequencyProducer = { progress -> 180f - progress * 80f },
            amplitudeEnvelope = { progress -> exp(-progress.toDouble() * 6.0).toFloat() }
        )
    }

    fun playCombo(combo: Int) {
        val baseFreq = (440f + (combo.coerceAtMost(10) * 55f))
        playTone(
            durationMs = 80,
            frequencyProducer = { baseFreq },
            amplitudeEnvelope = { 1f - it }
        )
    }

    fun playTick() {
        playTone(
            durationMs = 30,
            frequencyProducer = { 880f },
            amplitudeEnvelope = { 1f - it }
        )
    }

    fun playWin() {
        if (!isEnabled) return
        scope.launch {
            val notes = listOf(523.25f, 659.25f, 783.99f, 1046.50f)
            for (note in notes) {
                playTone(
                    durationMs = 120,
                    frequencyProducer = { note },
                    amplitudeEnvelope = { 1f - it * 0.7f }
                )
                kotlinx.coroutines.delay(100)
            }
        }
    }
}
