package com.zereshk.suroi

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.math.abs

class Shake(
    lcOwner: LifecycleOwner,
    private val sensor: SensorManager,
    private val onShake: () -> Unit = {}
) : SensorEventListener, DefaultLifecycleObserver {
    private var start: Long = 0; private var end: Long = 0; private var shakes = 0
    private var lastX = 0f; private var lastY = 0f; private var lastZ = 0f
    init {
        sensor.registerListener(this,
        sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI)
        lcOwner.lifecycle.addObserver(this)
    }
    override fun onResume(owner: LifecycleOwner) {
        sensor.registerListener(this,
        sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI)
    }
    override fun onPause(owner: LifecycleOwner) { sensor.unregisterListener(this) }
    override fun onSensorChanged(se: SensorEvent) {
        val x = se.values[0]; val y = se.values[1]; val z = se.values[2]
        val totalMovement = abs(x + y + z - lastX - lastY - lastZ)
        if (totalMovement > MIN_FORCE) {
            val now = System.currentTimeMillis()
            if (start == 0L) { start = now; end = now }
            if (now - end < MAX_DELAY) {
                end = now; shakes++
                lastX = x; lastY = y; lastZ = z
                if (shakes >= MIN_SHAKES) { if (now - start < MAX_DURATION) { onShake(); reset() } }
            } else { reset() }
        }
    }
    private fun reset() {
        start = 0; shakes = 0; end = 0
        lastX = 0f; lastY = 0f; lastZ = 0f
    }
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    companion object {
        private const val MIN_FORCE = 5; private const val MIN_SHAKES = 3
        private const val MAX_DELAY = 200; private const val MAX_DURATION = 600
    }
}