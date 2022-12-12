package com.example.virtualeye

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.ImageButton

class MainActivity : AppCompatActivity(), SensorEventListener {

    // variable init
    private lateinit var sensorManager: SensorManager
    private lateinit var tts: TextToSpeech

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Hide Action Bar - For UI purpose
        supportActionBar?.hide()

        tts = TextToSpeech(this, TextToSpeech.OnInitListener {
            if (it == TextToSpeech.SUCCESS) {
                tts.speak("Hello, welcome to Virtual Eye! Please shake the phone to start assisted navigation.", TextToSpeech.QUEUE_ADD, null, null)
            }
        })

        // Init Sensor Manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Navigate to Regular Navigation Screen - button click
        val regular_nav_bt = findViewById<ImageButton>(R.id.regular_navigation_bt)
        regular_nav_bt.setOnClickListener{
            val intent_regular_nav_bt = Intent(this, RegularNavigation::class.java)
            startActivity(intent_regular_nav_bt)
        }

        // Navigate to Assisted Navigation Screen - button click
        val assisted_nav_bt = findViewById<ImageButton>(R.id.asst_nav_bt)
        assisted_nav_bt.setOnClickListener{
            val intent_assisted_nav_bt = Intent(this, AssistedNavigation::class.java)
            startActivity(intent_assisted_nav_bt)
        }
    }

    // Sensor change functions
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL)
    }


    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val acceleration = (x * x + y * y + z * z) /
                    (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH)

            //  If the acceleration is greater than 2, it indicates that the phone has been shaken
            if (acceleration > 2) {
                // Move to the assisted navigation screen here
                val intent = Intent(this, AssistedNavigation::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed - placeholder function
    }

}