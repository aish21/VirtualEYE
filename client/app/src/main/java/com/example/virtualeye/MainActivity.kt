package com.example.virtualeye

// Required Imports
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

    /**
     * The MainActivity class is the main entry point for the VirtualEYE app. It initializes the app's
     * sensor manager, text-to-speech engine, and UI components, and provides functionality for
     * navigating to other screens.
     */

    // Variable Initialization

    // Sensor Manager for device sensors
    private lateinit var sensorManager: SensorManager
    // Text-to-Speech Engine for spoken feedback
    private lateinit var tts: TextToSpeech

    /**
     * This method is called when the activity is first created. It initializes the app's UI,
     * hides the action bar, and sets up the text-to-speech engine.
     *
     * @param savedInstanceState a Bundle object containing the activity's previously saved state.
     */

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Hide Action Bar - For UI purpose
        supportActionBar?.hide()

        // Initialize Text-to-Speech Engine
        tts = TextToSpeech(this) {
            if (it == TextToSpeech.SUCCESS) {
                tts.setSpeechRate(0.85f)
                tts.speak(
                    "Hello, welcome to Virtual Eye! Please shake the phone to start assisted navigation.",
                    TextToSpeech.QUEUE_ADD,
                    null,
                    null
                )
            }
        }

        // Initialize Sensor Manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Navigate to Regular Navigation Screen - button click
        val regular_nav_bt = findViewById<ImageButton>(R.id.regular_navigation_bt)
        regular_nav_bt.setOnClickListener{
            val intent_regular_nav_bt = Intent(this, RegularNavigation::class.java)
            tts.shutdown()
            startActivity(intent_regular_nav_bt)
        }

        // Navigate to Assisted Navigation Screen - button click
        val assisted_nav_bt = findViewById<ImageButton>(R.id.asst_nav_bt)
        assisted_nav_bt.setOnClickListener{
            val intent_assisted_nav_bt = Intent(this, AssistedNavigation::class.java)
            tts.shutdown()
            startActivity(intent_assisted_nav_bt)
        }
    }

    /**
     * This method is called when the activity is resumed. It registers the app's accelerometer sensor
     * listener with the sensor manager.
     */

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL)
    }

    /**
     * This method is called when the activity is paused. It unregisters the app's accelerometer sensor
     * listener from the sensor manager.
     */

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    /**
     * This method is called when the app's accelerometer sensor detects a change in acceleration.
     * If the acceleration is greater than 2, it indicates that the phone has been shaken, and the
     * app navigates to the assisted navigation screen.
     *
     * @param event the SensorEvent object containing the sensor's latest data.
     */

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
                tts.shutdown()
                startActivity(intent)
            }
        }
    }

    // Not needed - placeholder function
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed - placeholder function
    }

}