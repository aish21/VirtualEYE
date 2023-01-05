package com.example.virtualeye

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.Sensor.TYPE_MAGNETIC_FIELD
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class BlindNav : AppCompatActivity(), SensorEventListener {

    // Imports
    private lateinit var sensorManager: SensorManager
    var compassDir: String? = null
    private lateinit var tts: TextToSpeech
    var rssiVal: Int? = null
    var bleMAC: String? = null
    var tempVal: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        // Init Sensor Manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD)

        // Recurring Tasks
        val timerBLE = Timer()
        val timerBLETask = object: TimerTask() {
            @SuppressLint("MissingPermission")
            override fun run() {
                // BLE Scanner Init
                val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
                if (bluetoothAdapter == null) {
                    // Toast.makeText(this, "Bluetooth", Toast.LENGTH_SHORT).show()
                    finish()
                }

                mBluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
                mScanCallback = initCallbacks()
                //println(mScanCallback.toString())
                //println("mScanCallback")
                mBluetoothLeScanner?.startScan(mScanCallback)
            }
        }
        val timerBLECheck = Timer()
        val timerBLECheckTask = object: TimerTask() {
            override fun run() {
                if(rssiVal != null && bleMAC != null){
                    if(rssiVal!! > -70){
                        if(bleMAC != tempVal){
                            callTTS(bleMAC!!)
                            tempVal = bleMAC
                        }
                    }
                }
            }
        }

        timerBLE.scheduleAtFixedRate(timerBLETask, 0, 1000)
        timerBLECheck.scheduleAtFixedRate(timerBLECheckTask, 0, 2000)
    }

    // Functions to support the working of sensors
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this, accelerometer)
        sensorManager.unregisterListener(this, magnetometer)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor === accelerometer) {
            lowPass(event.values, lastAccelerometer)
            lastAccelerometerSet = true
        } else if (event?.sensor === magnetometer) {
            lowPass(event.values, lastMagnetometer)
            lastMagnetometerSet = true
        }

        if (lastAccelerometerSet && lastMagnetometerSet) {
            val r = FloatArray(9)
            if (SensorManager.getRotationMatrix(r, null, lastAccelerometer, lastMagnetometer)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(r, orientation)
                val degree = (Math.toDegrees(orientation[0].toDouble()) + 360).toFloat() % 360
                currentDegree = degree
            }
        }

        if (currentDegree >= 315 || currentDegree < 45) {
            // North
            compassDir = "N"

        } else if (currentDegree >= 45 && currentDegree < 135) {
            // East
            compassDir = "E"

        } else if (currentDegree >= 135 && currentDegree < 225) {
            // South
            compassDir = "S"

        } else if (currentDegree >= 225 && currentDegree < 315) {
            // West
            compassDir = "W"
        }
        Log.i("Compass", "Direction: $compassDir")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed - placeholder function
    }

    private fun lowPass(input: FloatArray, output: FloatArray) {
        val alpha = 0.05f
        for (i in input.indices) {
            output[i] = output[i] + alpha * (input[i] - output[i])
        }
    }

    private fun vibratePhone() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) { // Vibrator availability checking
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE)) // New vibrate method for API Level 26 or higher
            } else {
                vibrator.vibrate(1000) // Vibrate method for below API Level 26
            }
        }
    }

    private fun initCallbacks(): ScanCallback {
        val name1 = "FCL Beacon1"
        val name2 = "FWM8BLZ02"

        return object : ScanCallback() {
            @SuppressLint("MissingPermission", "NewApi")
            override fun onScanResult(
                callbackType: Int,
                result: ScanResult
            ) {
                super.onScanResult(callbackType, result)

                if (result.device != null) {
                    if(result.device.name != null) {
                        //addDevice(result.getDevice(), result.getRssi());
                        if(result.device.name.equals(name1) or result.device.name.equals(name2)) {
                            Log.i("BLE NAME: ", result.device.name)
                            //println(result.device.name)
                            Log.i("BLE MAC: ", result.device.toString())
                            bleMAC = result.device.toString()
                            //println(result.device)
                            Log.i("BLE RSSI: ", result.rssi.toString())
                            rssiVal = result.rssi
                            //println(result.rssi)
                        }
                    }
                }
                return
            }
        }
    }

    private fun callTTS(input: String) {
        val dictBLE: MutableMap<String, String> = mutableMapOf()
        dictBLE["Cybercrime Analysis & Research Alliance @ NTU (CARA)"] = "E4:7E:DB:B2:0D:3C"
        dictBLE["SCSE Student Lounge"] = "E3:2D:87:49:E5:BF"
        dictBLE["Hardware Lab 1"] = "CF:BD:6D:B7:8E:7D"
        dictBLE["Hardware Lab 2"] = "D8:3F:BB:F5:EF:5E"
        dictBLE["Software Lab 1"] = "F1:C6:5F:C8:71:9D"
        dictBLE["Software Lab 2"] = "D6:65:D2:8F:C5:8F"
        dictBLE["Hardware Projects Lab"] = "E9:4E:48:02:C6:84"

        if (dictBLE.containsValue(input)) {
            val key = dictBLE.filter { it.value == input }.keys.first()
            if (key == selectedDest) {
                tts = TextToSpeech(this) {
                    if (it == TextToSpeech.SUCCESS) {
                        tts.setSpeechRate(0.95f)
                        tts.speak(
                            "You have arrived at your destination!", TextToSpeech.QUEUE_FLUSH,
                            null,
                            null
                        )
                    }
                }
            } else {
                tts = TextToSpeech(this) {
                    if (it == TextToSpeech.SUCCESS) {
                        tts.setSpeechRate(0.95f)
                        tts.speak(
                            "You have arrived at$key", TextToSpeech.QUEUE_FLUSH,
                            null,
                            null
                        )
                    }
                }
            }
        }
    }

    private fun navInit(){

    }
}