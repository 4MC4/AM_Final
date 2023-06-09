package com.android.example.automation_control_helper

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.graphics.PorterDuff

private data class AccelerometerData(val x: Float, val y: Float)

class FragmentD : Fragment(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null
    private var accelerometerSensor: Sensor? = null
    private lateinit var textViewBrightness: TextView
    private lateinit var progressBarBrightness: ProgressBar
    private lateinit var imageViewArrow: ImageView

    private val accelerometerBuffer: Array<AccelerometerData> = Array(10) { AccelerometerData(0f, 0f) }
    private var accelerometerBufferIndex: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_d, container, false)
        textViewBrightness = rootView.findViewById(R.id.textViewBrightness)
        progressBarBrightness = rootView.findViewById(R.id.progressBarBrightness)
        imageViewArrow = rootView.findViewById(R.id.imageView2)
        return rootView
    }

    override fun onResume() {
        super.onResume()
        lightSensor?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        accelerometerSensor?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_LIGHT -> {
                val lux = event.values[0]
                textViewBrightness.text = getString(R.string.brightness, lux)
                updateProgressBar(lux)
            }
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]

                if (x*x+y*y>1.0) {
                    updateAccelerometerBuffer(x, y)
                }

                val averageX = calculateAverage(accelerometerBuffer.map { it.x })
                val averageY = calculateAverage(accelerometerBuffer.map { it.y })

                val rotationAngle = Math.toDegrees(Math.atan2(averageX.toDouble(), averageY.toDouble())).toFloat()
                imageViewArrow.rotation = -rotationAngle
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun updateProgressBar(lux: Float) {
        val maxBrightness = 500
        val progress = (lux / maxBrightness * 100).toInt()
        progressBarBrightness.progress = progress

        val color = when {
            progress < 60 -> ContextCompat.getColor(requireContext(), R.color.red)
            progress < 100 -> ContextCompat.getColor(requireContext(), R.color.yellow)
            else -> ContextCompat.getColor(requireContext(), R.color.green)
        }
        progressBarBrightness.progressDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }

    private fun updateAccelerometerBuffer(x: Float, y: Float) {
        accelerometerBuffer[accelerometerBufferIndex] = AccelerometerData(x, y)
        accelerometerBufferIndex = (accelerometerBufferIndex + 1) % accelerometerBuffer.size
    }

    private fun calculateAverage(values: List<Float>): Float {
        return values.average().toFloat()
    }
}