package com.example.virtualeye

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.*
import android.content.ContentValues.TAG
import android.content.Context
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.Sensor.TYPE_MAGNETIC_FIELD
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_GAME
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.RotateAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import java.lang.Math.toDegrees
import java.util.*


var mBluetoothLeScanner: BluetoothLeScanner? = null
var mScanCallback: ScanCallback? = null
var selectedStart: String? = null
var selectedDest: String? = null
@SuppressLint("StaticFieldLeak")
lateinit var image: ImageView
lateinit var sensorManager: SensorManager
lateinit var accelerometer: Sensor
lateinit var magnetometer: Sensor
var currentDegree = 0.0f
var lastAccelerometer = FloatArray(3)
var lastMagnetometer = FloatArray(3)
var lastAccelerometerSet = false
var lastMagnetometerSet = false
var rssiVal: Int? = null
var bleMAC: String? = null
private lateinit var tts: TextToSpeech
var tempVal: String? = null


@Suppress("DEPRECATION")
class RegularNavigation : AppCompatActivity(), SensorEventListener {

//    // BluetoothAdapter is required for any and all Bluetooth activity.
//    var btAdapter: BluetoothAdapter? = null
//
//    // BluetoothLeScanner is required for scanning for BLE devices.
//    var btScanner: BluetoothLeScanner? = null
//
//    // A ScanCallback is required to process the scan results.
//    val scanCallback = object : ScanCallback() {
//        override fun onScanResult(callbackType: Int, result: ScanResult) {
//            processResult(result)
//        }
//
//        override fun onBatchScanResults(results: List<ScanResult>) {
//            for (result in results) {
//                processResult(result)
//            }
//        }
//
//        override fun onScanFailed(errorCode: Int) {
//            Log.e(TAG, "BLE scan failed with code $errorCode")
//        }
//
//        @SuppressLint("MissingPermission")
//        private fun processResult(result: ScanResult) {
//            // Do something with the scan result here.
//            val device: BluetoothDevice = result.device
//            val rssi: Int = result.rssi
//            val name: String = device.name
//            Log.i(TAG, "Found device: $name with RSSI: $rssi")
//        }
//    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.regular_navigation)
        // Hide Action Bar - For UI purpose
        supportActionBar?.hide()

        image = findViewById(R.id.imageViewCompass)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD)

        val spinner_start: Spinner = findViewById(R.id.start_point)
        val spinner_dest: Spinner = findViewById(R.id.destination)

        val start_loc = listOf("NULL",
            "Cybercrime Analysis & Research Alliance @ NTU (CARA)",
            "SCSE Student Lounge",
            "Hardware Lab 1",
            "Hardware Lab 2",
            "Software Lab 1",
            "Software Lab 2",
            "Hardware Projects Lab")

        val dest_loc = listOf("NULL",
            "Cybercrime Analysis & Research Alliance @ NTU (CARA)",
            "SCSE Student Lounge",
            "Hardware Lab 1",
            "Hardware Lab 2",
            "Software Lab 1",
            "Software Lab 2",
            "Hardware Projects Lab")

        val button = findViewById<ImageButton>(R.id.startScanButton)
        val imageView = findViewById<View>(R.id.imageView) as SubsamplingScaleImageView
        imageView.setImage(ImageSource.resource(R.drawable.scse_1_final))
        val dirImage = findViewById<ImageView>(R.id.directions_view)

        val adapter_start = ArrayAdapter(this, android.R.layout.simple_spinner_item, start_loc)
        val adapter_dest = ArrayAdapter(this, android.R.layout.simple_spinner_item, dest_loc)
        spinner_start.adapter = adapter_start
        spinner_dest.adapter = adapter_dest
        adapter_start.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter_dest.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner_start.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Do something when an item is selected
                selectedStart = parent.getItemAtPosition(position).toString()

                when (selectedStart) {
                    "NULL" -> {
                        spinner_start.foreground = getDrawable(R.drawable.start_drp)
                    }
                    "Cybercrime Analysis & Research Alliance @ NTU (CARA)" -> {
                        spinner_start.foreground = getDrawable(R.drawable.cara_drp)
                    }
                    "SCSE Student Lounge" -> {
                        spinner_start.foreground = getDrawable(R.drawable.lounge_drp)
                    }
                    "Hardware Lab 1" -> {
                        spinner_start.foreground = getDrawable(R.drawable.hw1_drp)
                    }
                    "Hardware Lab 2" -> {
                        spinner_start.foreground = getDrawable(R.drawable.hw2_drp)
                    }
                    "Software Lab 1" -> {
                        spinner_start.foreground = getDrawable(R.drawable.sw1_drp)
                    }
                    "Software Lab 2" -> {
                        spinner_start.foreground = getDrawable(R.drawable.sw2_drp)
                    }
                    "Hardware Projects Lab" -> {
                        spinner_start.foreground = getDrawable(R.drawable.hwproj_drp)
                    }
                }
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do something when no item is selected
                spinner_start.foreground = getDrawable(R.drawable.start_drp)
            }
        }

        spinner_dest.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Do something when an item is selected
                selectedDest = parent.getItemAtPosition(position).toString()

                when (selectedDest) {
                    "NULL" -> {
                        spinner_dest.foreground = getDrawable(R.drawable.dest_drp)
                    }
                    "Cybercrime Analysis & Research Alliance @ NTU (CARA)" -> {
                        spinner_dest.foreground = getDrawable(R.drawable.cara_drp)
                    }
                    "SCSE Student Lounge" -> {
                        spinner_dest.foreground = getDrawable(R.drawable.lounge_drp)
                    }
                    "Hardware Lab 1" -> {
                        spinner_dest.foreground = getDrawable(R.drawable.hw1_drp)
                    }
                    "Hardware Lab 2" -> {
                        spinner_dest.foreground = getDrawable(R.drawable.hw2_drp)
                    }
                    "Software Lab 1" -> {
                        spinner_dest.foreground = getDrawable(R.drawable.sw1_drp)
                    }
                    "Software Lab 2" -> {
                        spinner_dest.foreground = getDrawable(R.drawable.sw2_drp)
                    }
                    "Hardware Projects Lab" -> {
                        spinner_dest.foreground = getDrawable(R.drawable.hwproj_drp)
                    }
                }
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do something when no item is selected
                spinner_dest.foreground = getDrawable(R.drawable.dest_drp)
            }
        }

        val timerBLE = Timer()
        val timerBLETask = object: TimerTask() {
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

                val settings = ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build()

                val filter1 = ScanFilter.Builder()
                    .setDeviceName("FCL Beacon1")
                    .build()

                val filter2 = ScanFilter.Builder()
                    .setDeviceName("FWM8BLZ02")
                    .build()

                val filters = mutableListOf(filter1, filter2)

                mBluetoothLeScanner?.startScan(filters, settings, mScanCallback)
                println("here")
            }
        }
        val timerBLECheck = Timer()
        val timerBLECheckTask = object: TimerTask() {
            override fun run() {

                if(rssiVal != null && bleMAC != null){
                    println("here2")
                    if(rssiVal!! > -75){
                        println("here3")
                        if(bleMAC != tempVal){
                            println("here4")
                            callTTS(bleMAC!!)
                            tempVal = bleMAC
                        }
                    }
                }
            }
        }

        button.setOnClickListener {
//            startScanning(this)

            timerBLE.scheduleAtFixedRate(timerBLETask, 0, 10000)
            timerBLECheck.scheduleAtFixedRate(timerBLECheckTask, 0, 2000)

            // Change map based on selected values in drop down
            if(selectedStart != "NULL" && selectedDest != "NULL"){

                // CARA -> Dest
                if(selectedStart == "Cybercrime Analysis & Research Alliance @ NTU (CARA)" && selectedDest == "SCSE Student Lounge"){
                    imageView.setImage(ImageSource.resource(R.drawable.cara_lounge))
                    dirImage.setImageResource(R.drawable.dir_cara_lounge)
                }
                else if(selectedStart == "Cybercrime Analysis & Research Alliance @ NTU (CARA)" && selectedDest == "Hardware Lab 1"){
                    imageView.setImage(ImageSource.resource(R.drawable.cara_hw1))
                    dirImage.setImageResource(R.drawable.dir_cara_hw1)
                }
                else if(selectedStart == "Cybercrime Analysis & Research Alliance @ NTU (CARA)" && selectedDest == "Hardware Lab 2"){
                    imageView.setImage(ImageSource.resource(R.drawable.cara_hw2))
                    dirImage.setImageResource(R.drawable.dir_cara_hw2)
                }
                else if(selectedStart == "Cybercrime Analysis & Research Alliance @ NTU (CARA)" && selectedDest == "Software Lab 1"){
                    imageView.setImage(ImageSource.resource(R.drawable.cara_sw1))
                    dirImage.setImageResource(R.drawable.dir_cara_sw1)
                }
                else if(selectedStart == "Cybercrime Analysis & Research Alliance @ NTU (CARA)" && selectedDest == "Software Lab 2"){
                    imageView.setImage(ImageSource.resource(R.drawable.cara_sw2))
                    dirImage.setImageResource(R.drawable.dir_cara_sw2)
                }
                else if(selectedStart == "Cybercrime Analysis & Research Alliance @ NTU (CARA)" && selectedDest == "Hardware Projects Lab"){
                    imageView.setImage(ImageSource.resource(R.drawable.cara_hwproj))
                    dirImage.setImageResource(R.drawable.dir_cara_hwproj)
                }

                // SCSE Student Lounge -> Dest
                if(selectedStart == "SCSE Student Lounge" && selectedDest == "Cybercrime Analysis & Research Alliance @ NTU (CARA)"){
                    imageView.setImage(ImageSource.resource(R.drawable.cara_lounge))
                    dirImage.setImageResource(R.drawable.dir_lounge_cara)
                }
                else if(selectedStart == "SCSE Student Lounge" && selectedDest == "Hardware Lab 1"){
                    imageView.setImage(ImageSource.resource(R.drawable.hw1_lounge))
                    dirImage.setImageResource(R.drawable.dir_lounge_hw1)
                }
                else if(selectedStart == "SCSE Student Lounge" && selectedDest == "Hardware Lab 2"){
                    imageView.setImage(ImageSource.resource(R.drawable.hw2_lounge))
                    dirImage.setImageResource(R.drawable.dir_lounge_hw2)
                }
                else if(selectedStart == "SCSE Student Lounge" && selectedDest == "Software Lab 1"){
                    imageView.setImage(ImageSource.resource(R.drawable.lounge_sw1))
                    dirImage.setImageResource(R.drawable.dir_lounge_sw1)
                }
                else if(selectedStart == "SCSE Student Lounge" && selectedDest == "Software Lab 2"){
                    imageView.setImage(ImageSource.resource(R.drawable.lounge_sw2))
                    dirImage.setImageResource(R.drawable.dir_lounge_sw2)
                }
                else if(selectedStart == "SCSE Student Lounge" && selectedDest == "Hardware Projects Lab"){
                    imageView.setImage(ImageSource.resource(R.drawable.lounge_hwproj))
                    dirImage.setImageResource(R.drawable.dir_lounge_hwproj)
                }

                // Software Lab 1 -> Dest
                if(selectedStart == "Software Lab 1" && selectedDest == "Cybercrime Analysis & Research Alliance @ NTU (CARA)"){
                    imageView.setImage(ImageSource.resource(R.drawable.cara_sw1))
                    dirImage.setImageResource(R.drawable.dir_sw1_cara)
                }
                else if(selectedStart == "Software Lab 1" && selectedDest == "Hardware Lab 1"){
                    imageView.setImage(ImageSource.resource(R.drawable.hw1_sw1))
                    dirImage.setImageResource(R.drawable.dir_sw1_hw1)
                }
                else if(selectedStart == "Software Lab 1" && selectedDest == "Hardware Lab 2"){
                    imageView.setImage(ImageSource.resource(R.drawable.hw2_sw1))
                    dirImage.setImageResource(R.drawable.dir_sw1_hw2)
                }
                else if(selectedStart == "Software Lab 1" && selectedDest == "SCSE Student Lounge"){
                    imageView.setImage(ImageSource.resource(R.drawable.lounge_sw1))
                    dirImage.setImageResource(R.drawable.dir_sw1_lounge)
                }
                else if(selectedStart == "Software Lab 1" && selectedDest == "Software Lab 2"){
                    imageView.setImage(ImageSource.resource(R.drawable.sw1_sw2))
                    dirImage.setImageResource(R.drawable.dir_sw1_sw2)
                }
                else if(selectedStart == "Software Lab 1" && selectedDest == "Hardware Projects Lab"){
                    imageView.setImage(ImageSource.resource(R.drawable.sw1_hwproj))
                    dirImage.setImageResource(R.drawable.dir_sw1_hwproj)
                }

                // Hardware Lab 1 -> Dest
                if(selectedStart == "Hardware Lab 1" && selectedDest == "Cybercrime Analysis & Research Alliance @ NTU (CARA)"){
                    imageView.setImage(ImageSource.resource(R.drawable.cara_hw1))
                    dirImage.setImageResource(R.drawable.dir_hw1_cara)
                }
                else if(selectedStart == "Hardware Lab 1" && selectedDest == "SCSE Student Lounge"){
                    imageView.setImage(ImageSource.resource(R.drawable.hw1_lounge))
                    dirImage.setImageResource(R.drawable.dir_hw1_lounge)
                }
                else if(selectedStart == "Hardware Lab 1" && selectedDest == "Hardware Lab 2"){
                    imageView.setImage(ImageSource.resource(R.drawable.hw1_hw2))
                    dirImage.setImageResource(R.drawable.dir_hw1_hw2)
                }
                else if(selectedStart == "Hardware Lab 1" && selectedDest == "Software Lab 1"){
                    imageView.setImage(ImageSource.resource(R.drawable.hw1_sw1))
                    dirImage.setImageResource(R.drawable.dir_hw1_sw1)
                }
                else if(selectedStart == "Hardware Lab 1" && selectedDest == "Software Lab 2"){
                    imageView.setImage(ImageSource.resource(R.drawable.hw1_sw2))
                    dirImage.setImageResource(R.drawable.dir_hw1_sw2)
                }
                else if(selectedStart == "Hardware Lab 1" && selectedDest == "Hardware Projects Lab"){
                    imageView.setImage(ImageSource.resource(R.drawable.hw1_hwproj))
                    dirImage.setImageResource(R.drawable.dir_hw1_hwproj)
                }

                // Hardware Lab 2 -> Dest
                if(selectedStart == "Hardware Lab 2" && selectedDest == "Cybercrime Analysis & Research Alliance @ NTU (CARA)"){
                    imageView.setImage(ImageSource.resource(R.drawable.cara_hw2))
                    dirImage.setImageResource(R.drawable.dir_hw2_cara)
                }
                else if(selectedStart == "Hardware Lab 2" && selectedDest == "SCSE Student Lounge"){
                    imageView.setImage(ImageSource.resource(R.drawable.hw2_lounge))
                    dirImage.setImageResource(R.drawable.dir_hw2_lounge)
                }
                else if(selectedStart == "Hardware Lab 2" && selectedDest == "Hardware Lab 1"){
                    imageView.setImage(ImageSource.resource(R.drawable.hw1_hw2))
                    dirImage.setImageResource(R.drawable.dir_hw2_hw1)
                }
                else if(selectedStart == "Hardware Lab 2" && selectedDest == "Software Lab 1"){
                    imageView.setImage(ImageSource.resource(R.drawable.hw2_sw1))
                    dirImage.setImageResource(R.drawable.dir_hw2_sw1)
                }
                else if(selectedStart == "Hardware Lab 2" && selectedDest == "Software Lab 2"){
                    imageView.setImage(ImageSource.resource(R.drawable.hw2_sw2))
                    dirImage.setImageResource(R.drawable.dir_hw2_sw2)
                }
                else if(selectedStart == "Hardware Lab 2" && selectedDest == "Hardware Projects Lab"){
                    imageView.setImage(ImageSource.resource(R.drawable.hw2_hwproj))
                    dirImage.setImageResource(R.drawable.dir_hw2_hwproj)
                }

                // Software Lab 1 -> Dest
                if(selectedStart == "Software Lab 1" && selectedDest == "Cybercrime Analysis & Research Alliance @ NTU (CARA)"){
                    imageView.setImage(ImageSource.resource(R.drawable.cara_sw1))
                    dirImage.setImageResource(R.drawable.dir_sw1_cara)
                }
                else if(selectedStart == "Software Lab 1" && selectedDest == "SCSE Student Lounge"){
                    imageView.setImage(ImageSource.resource(R.drawable.lounge_sw1))
                    dirImage.setImageResource(R.drawable.dir_sw1_lounge)
                }
                else if(selectedStart == "Software Lab 1" && selectedDest == "Hardware Lab 1"){
                    imageView.setImage(ImageSource.resource(R.drawable.hw1_sw1))
                    dirImage.setImageResource(R.drawable.dir_sw1_hw1)
                }
                else if(selectedStart == "Software Lab 1" && selectedDest == "Hardware Lab 2"){
                    imageView.setImage(ImageSource.resource(R.drawable.hw2_sw1))
                    dirImage.setImageResource(R.drawable.dir_sw1_hw2)
                }
                else if(selectedStart == "Software Lab 1" && selectedDest == "Software Lab 2"){
                    imageView.setImage(ImageSource.resource(R.drawable.sw1_sw2))
                    dirImage.setImageResource(R.drawable.dir_sw1_sw2)
                }
                else if(selectedStart == "Software Lab 1" && selectedDest == "Hardware Projects Lab"){
                    imageView.setImage(ImageSource.resource(R.drawable.sw1_hwproj))
                    dirImage.setImageResource(R.drawable.dir_sw1_hwproj)
                }

                // Software Lab 2 -> Dest
                if(selectedStart == "Software Lab 2" && selectedDest == "Cybercrime Analysis & Research Alliance @ NTU (CARA)"){
                    imageView.setImage(ImageSource.resource(R.drawable.cara_sw2))
                    dirImage.setImageResource(R.drawable.dir_sw2_cara)
                }
                else if(selectedStart == "Software Lab 2" && selectedDest == "SCSE Student Lounge"){
                    imageView.setImage(ImageSource.resource(R.drawable.lounge_sw2))
                    dirImage.setImageResource(R.drawable.dir_sw2_lounge)
                }
                else if(selectedStart == "Software Lab 2" && selectedDest == "Hardware Lab 1"){
                    imageView.setImage(ImageSource.resource(R.drawable.hw1_sw2))
                    dirImage.setImageResource(R.drawable.dir_sw2_hw1)
                }
                else if(selectedStart == "Software Lab 2" && selectedDest == "Hardware Lab 2"){
                    imageView.setImage(ImageSource.resource(R.drawable.hw2_sw2))
                    dirImage.setImageResource(R.drawable.dir_sw2_hw2)
                }
                else if(selectedStart == "Software Lab 2" && selectedDest == "Software Lab 1"){
                    imageView.setImage(ImageSource.resource(R.drawable.sw1_sw2))
                    dirImage.setImageResource(R.drawable.dir_sw2_sw1)
                }
                else if(selectedStart == "Software Lab 2" && selectedDest == "Hardware Projects Lab"){
                    imageView.setImage(ImageSource.resource(R.drawable.sw2_hwproj))
                    dirImage.setImageResource(R.drawable.dir_sw2_hwproj)
                }

                // Hardware Projects Lab -> Dest
                if(selectedStart == "Hardware Projects Lab" && selectedDest == "Cybercrime Analysis & Research Alliance @ NTU (CARA)"){
                    imageView.setImage(ImageSource.resource(R.drawable.cara_hwproj))
                    dirImage.setImageResource(R.drawable.dir_hwproj_cara)
                }
                else if(selectedStart == "Hardware Projects Lab" && selectedDest == "SCSE Student Lounge"){
                    imageView.setImage(ImageSource.resource(R.drawable.lounge_hwproj))
                    dirImage.setImageResource(R.drawable.dir_hwproj_lounge)
                }
                else if(selectedStart == "Hardware Projects Lab" && selectedDest == "Hardware Lab 1"){
                    imageView.setImage(ImageSource.resource(R.drawable.hw1_hwproj))
                    dirImage.setImageResource(R.drawable.dir_hwproj_hw1)
                }
                else if(selectedStart == "Hardware Projects Lab" && selectedDest == "Hardware Lab 2"){
                    imageView.setImage(ImageSource.resource(R.drawable.hw2_hwproj))
                    dirImage.setImageResource(R.drawable.dir_hwproj_hw2)
                }
                else if(selectedStart == "Hardware Projects Lab" && selectedDest == "Software Lab 1"){
                    imageView.setImage(ImageSource.resource(R.drawable.sw1_hwproj))
                    dirImage.setImageResource(R.drawable.dir_hwproj_sw1)
                }
                else if(selectedStart == "Hardware Projects Lab" && selectedDest == "Software Lab 2"){
                    imageView.setImage(ImageSource.resource(R.drawable.sw2_hwproj))
                    dirImage.setImageResource(R.drawable.dir_hwproj_sw2)
                }
            }
            else{
                if(selectedStart == selectedDest){
                    Toast.makeText(this, "Start and Destination cannot be the same location, try again!", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this, "One or more values are NULL! Select a location from dropdown", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

//    @SuppressLint("MissingPermission")
//    fun startScanning(context: Context) {
//        // Get the Bluetooth adapter and scanner.
//        btAdapter = BluetoothAdapter.getDefaultAdapter()
//        btScanner = btAdapter?.bluetoothLeScanner
//
//        // Start scanning.
//        btScanner?.startScan(scanCallback)
//    }
//
//    @SuppressLint("MissingPermission")
//    fun stopScanning() {
//        btScanner?.stopScan(scanCallback)
//    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, magnetometer, SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this, accelerometer)
        sensorManager.unregisterListener(this, magnetometer)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor === accelerometer) {
            lowPass(event.values, lastAccelerometer)
            lastAccelerometerSet = true
        } else if (event.sensor === magnetometer) {
            lowPass(event.values, lastMagnetometer)
            lastMagnetometerSet = true
        }

        if (lastAccelerometerSet && lastMagnetometerSet) {
            val r = FloatArray(9)
            if (SensorManager.getRotationMatrix(r, null, lastAccelerometer, lastMagnetometer)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(r, orientation)
                val degree = (toDegrees(orientation[0].toDouble()) + 360).toFloat() % 360

                val rotateAnimation = RotateAnimation(
                    currentDegree,
                    -degree,
                    RELATIVE_TO_SELF, 0.5f,
                    RELATIVE_TO_SELF, 0.5f)
                rotateAnimation.duration = 1000
                rotateAnimation.fillAfter = true

                image.startAnimation(rotateAnimation)
                currentDegree = -degree
            }
        }
    }

    private fun lowPass(input: FloatArray, output: FloatArray) {
        val alpha = 0.05f

        for (i in input.indices) {
            output[i] = output[i] + alpha * (input[i] - output[i])
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
        dictBLE["testNode"] = "CF:99:D4:6A:7A:75"

        if (dictBLE.containsValue(input)) {
            val key = dictBLE.filter { it.value == input }.keys.first()
            if(key == selectedDest){
                println("here5")
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
            }else{
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
}