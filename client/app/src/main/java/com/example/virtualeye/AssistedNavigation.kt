package com.example.virtualeye

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.Sensor.TYPE_MAGNETIC_FIELD
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_GAME
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.example.virtualeye.databinding.AssistedNavigationBinding
import com.example.virtualeye.utils.Draw
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.Math.toDegrees
import java.net.HttpURLConnection
import java.net.URL

class AssistedNavigation : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: AssistedNavigationBinding
    private lateinit var objectDetector: ObjectDetector
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var tts: TextToSpeech
    private lateinit var welcomeMsg_tts: TextToSpeech
    private lateinit var setPoints: TextToSpeech
    private lateinit var sensorManager: SensorManager
    private lateinit var sensorManager2: SensorManager
    private var mSpeechRecognizer: SpeechRecognizer? = null
    private var mIsListening = false
    val startList = listOf("cara", "student lounge", "hardware lab 1", "hardware lab 2", "software lab 1", "software lab 2", "hardware projects lab")
    val destList = listOf("cara", "student lounge", "hardware lab 1", "hardware lab 2", "software lab 1", "software lab 2", "hardware projects lab")
    var mBluetoothLeScanner: BluetoothLeScanner? = null
    var mScanCallback: ScanCallback? = null
    var BLEScanMac: String? = null
    var path = mutableListOf<String>()
    var directions = mutableListOf<String>()
    var compassDir: String? = null
    lateinit var accelerometer: Sensor
    lateinit var magnetometer: Sensor
    var currentDegree = 0.0f
    var lastAccelerometer = FloatArray(3)
    var lastMagnetometer = FloatArray(3)
    var lastAccelerometerSet = false
    var lastMagnetometerSet = false

    companion object {
        // This constant is needed to verify the audio permission result
        private const val ASR_PERMISSION_REQUEST_CODE = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()


        welcomeMsg_tts = TextToSpeech(this) {
            if (it == TextToSpeech.SUCCESS) {
                welcomeMsg_tts.setSpeechRate(0.95f)
                welcomeMsg_tts.speak(
                    "Welcome to Assisted Navigation! Shake phone to set the start point and destination in the format - START LOCATION to DESTINATION",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )
            }
        }

        // Init Sensor Manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager2 = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD)

        binding = DataBindingUtil.setContentView(this, R.layout.assisted_navigation)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider = cameraProvider)
        }, ContextCompat.getMainExecutor(this))

        val localModel = LocalModel.Builder()
            .setAssetFilePath("model.tflite")
            .build()

        val customObjectDetectorOptions = CustomObjectDetectorOptions.Builder(localModel)
            .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
            .enableClassification()
            .setClassificationConfidenceThreshold(0.75f)
            .setMaxPerObjectLabelCount(3)
            .build()

        objectDetector = ObjectDetection.getClient(customObjectDetectorOptions)
    }

    // Sensor change functions
    override fun onResume() {
        super.onResume()
        sensorManager2.registerListener(this,
            sensorManager2.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, accelerometer, SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, magnetometer, SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
//        sensorManager.unregisterListener(this)
        sensorManager.unregisterListener(this, accelerometer)
        sensorManager.unregisterListener(this, magnetometer)
        sensorManager2.unregisterListener(this)
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

//                GlobalScope.launch {
//                    // start a new coroutine in background and continue
//                    val result = async {  }.await()
//                    println("Result: $result")
//                }

//                if(tts.isSpeaking){
//                    tts.stop()
//                }
//                verifyAudioPermissions()
//                createSpeechRecognizer()
//
//                if (mIsListening) {
//                    handleSpeechEnd()
//                } else {
//                    if(tts.isSpeaking){
//                        tts.stop()
//                    }
//                    handleSpeechBegin()
//                }
            }
        }

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
                val degree = (toDegrees(orientation[0].toDouble()) + 360).toFloat() % 360
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

    private fun lowPass(input: FloatArray, output: FloatArray) {
        val alpha = 0.05f
        for (i in input.indices) {
            output[i] = output[i] + alpha * (input[i] - output[i])
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed - placeholder function
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ASR_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // audio permission granted
                Toast.makeText(this, "You can now use voice commands!", Toast.LENGTH_LONG).show()
            } else {
                // audio permission denied
                Toast.makeText(this, "Please provide microphone permission to use voice.", Toast.LENGTH_LONG).show()
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindPreview(cameraProvider: ProcessCameraProvider){
        var textToSay = "Undefined"
        val preview = Preview.Builder().build()
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(binding.previewView.surfaceProvider)
        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280,720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val image = imageProxy.image
            if (image != null) {
                val processImage = InputImage.fromMediaImage(image, rotationDegrees)

                objectDetector.process(processImage)
                    .addOnSuccessListener { objects ->

                        for (i in objects) {

                            if(binding.parentLayout.childCount > 1) binding.parentLayout.removeViewAt(1)

                            val element = Draw(context = this,
                                rect = i.boundingBox,
                                text = i.labels.firstOrNull()?.text ?: "Undefined")

                            val objDet = i.labels.firstOrNull()?.text ?: "Undefined"

                            binding.parentLayout.addView(element)

                            tts = TextToSpeech(this) {
                                if (it == TextToSpeech.SUCCESS) {
                                    if (textToSay != objDet && !tts.isSpeaking) {
                                        tts.speak(
                                            objDet + "detected",
                                            TextToSpeech.QUEUE_FLUSH,
                                            null,
                                            null
                                        )
                                        textToSay = objDet
                                    }
                                }
                            }
                        }
                        imageProxy.close()
                    }.addOnFailureListener {
                        Log.v("AssistedNavigation", "ERROR - ${it.message}")
                        imageProxy.close()
                    }
            }
        }
        cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, imageAnalysis, preview)
    }

    private fun verifyAudioPermissions() {
        if (checkCallingOrSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                ASR_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun createSpeechRecognizer() {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        mSpeechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray) {}
            override fun onEndOfSpeech() {
                handleSpeechEnd()
            }

            override fun onError(error: Int) {
                handleSpeechEnd()
            }

            override fun onResults(results: Bundle) {
                // Called when recognition results are ready. This callback will be called when the
                // audio session has been completed and user utterance has been parsed.

                // This ArrayList contains the recognition results, if the list is non-empty,
                // handle the user utterance
                val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null && matches.size > 0) {
                    // The results are added in decreasing order of confidence to the list
                    val command = matches[0]
//                    mUserUtteranceOutput!!.text = command
                    handleCommand(command)
                }
            }

            override fun onPartialResults(partialResults: Bundle) {
                // Called when partial recognition results are available, this callback will be
                // called each time a partial text result is ready while the user is speaking.
                val matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null && matches.size > 0) {
                    // handle partial speech results
                    val partialText = matches[0]
//                    mUserUtteranceOutput!!.text = partialText
                }
            }

            override fun onEvent(eventType: Int, params: Bundle) {}
        })
    }

    private fun createIntent(): Intent {
        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        i.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
        return i
    }

    private fun handleSpeechBegin() {
        // start audio session
        mIsListening = true
        mSpeechRecognizer!!.startListening(createIntent())
    }

    private fun handleSpeechEnd() {
        // end audio session
        mIsListening = false
        mSpeechRecognizer!!.cancel()
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("MissingPermission")
    private fun handleCommand(command: String) {
        Toast.makeText(this, command, Toast.LENGTH_LONG).show()
        val dictBLE: MutableMap<String, String> = mutableMapOf()
        dictBLE["cara"] = "E4:7E:DB:B2:0D:3C"
        dictBLE["student lounge"] = "E3:2D:87:49:E5:BF"
        dictBLE["hardware lab 1"] = "CF:BD:6D:B7:8E:7D"
        dictBLE["hardware lab 2"] = "D8:3F:BB:F5:EF:5E"
        dictBLE["software lab 1"] = "F1:C6:5F:C8:71:9D"
        dictBLE["software lab 2"] = "D6:65:D2:8F:C5:8F"
        dictBLE["hardware projects lab"] = "E9:4E:48:02:C6:84"

        if(command.contains("to")){
            val (startPoint, destLoc) = splitString(command)
            Log.i("START AND DEST", startPoint + destLoc)
            if (startList.contains(startPoint)){
                if (destList.contains(destLoc)){

                    // BLE Scanner Init
                    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
                    if (bluetoothAdapter == null) {
                        Toast.makeText(this, "Bluetooth", Toast.LENGTH_SHORT).show()
                        finish()
                    }

                    tts = TextToSpeech(this) {
                        if (it == TextToSpeech.SUCCESS) {
                            tts.setSpeechRate(0.95f)
                            tts.speak(
                                "Calculating route from $startPoint to $destLoc",
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                null
                            )
                        }
                    }

                    // Send the start and dest to server
                    GlobalScope.launch {
                        Log.i("Check", "HERE")
                        val respJSON = sendRequest(startPoint, destLoc)
                        // Update the UI or do something with the response here
                        Log.i("RESP", respJSON.toString())

                        val pathTemp = respJSON.getJSONArray("path")
                        for (i in 0 until pathTemp.length()) {
                            path.add(pathTemp.get(i) as String)
                        }

                        val directionsTemp = respJSON.getJSONArray("directions")
                        for (i in 0 until directionsTemp.length()) {
                            directions.add(directionsTemp.get(i) as String)
                        }

                        println(path)
                        println(path::class)
                        println(directions)
                        println(directions::class)
                    }
                    //job.cancel()

                    // Start BLE scan
                    mBluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
                    mScanCallback = initCallbacks()
                    mBluetoothLeScanner?.startScan(mScanCallback)

                    // TODO - Process result from server

                    // Pair up the directions and locations
                    val navPoints = directions.zip(path)

                    // Start at the first location
                    var currentLocation = path.first()

                    // Navigate to the final destination - add while true before when and break
//                    for ((direction, location) in navPoints) {
//                        while (true) {
//                            when (direction) {
//                                "left" -> {
//                                    // Check if the user has truly turned left
//                                    if (isLeftTurn(currentLocation, location)) {
//
//                                        tts = TextToSpeech(this) {
//                                            if (it == TextToSpeech.SUCCESS) {
//                                                tts.setSpeechRate(0.95f)
//                                                tts.speak(
//                                                    "In 2 metres, turn left and continue straight!",
//                                                    TextToSpeech.QUEUE_FLUSH,
//                                                    null,
//                                                    null
//                                                )
//                                            }
//                                        }
//
//                                        // Update the current location
//                                        currentLocation = location
//                                        break
//                                    } else {
//                                        // The user didn't turn left, display an error message
//                                        tts = TextToSpeech(this) {
//                                            if (it == TextToSpeech.SUCCESS) {
//                                                tts.setSpeechRate(0.95f)
//                                                tts.speak(
//                                                    "Facing the wrong way!",
//                                                    TextToSpeech.QUEUE_FLUSH,
//                                                    null,
//                                                    null
//                                                )
//                                            }
//                                        }
//                                    }
//                                }
//                                "right" -> {
//                                    // Check if the user has truly turned right
//                                    if (isRightTurn(currentLocation, location)) {
//
//                                        tts = TextToSpeech(this) {
//                                            if (it == TextToSpeech.SUCCESS) {
//                                                tts.setSpeechRate(0.95f)
//                                                tts.speak(
//                                                    "In 2 metres, turn left and continue straight!",
//                                                    TextToSpeech.QUEUE_FLUSH,
//                                                    null,
//                                                    null
//                                                )
//                                            }
//                                        }
//
//                                        // Update the current location
//                                        currentLocation = location
//                                        break
//                                    } else {
//                                        // The user didn't turn right, display an error message
//                                        tts = TextToSpeech(this) {
//                                            if (it == TextToSpeech.SUCCESS) {
//                                                tts.setSpeechRate(0.95f)
//                                                tts.speak(
//                                                    "Facing the wrong way!",
//                                                    TextToSpeech.QUEUE_FLUSH,
//                                                    null,
//                                                    null
//                                                )
//                                            }
//                                        }
//                                    }
//                                }
//                                "straight" -> {
//                                    // Check if the user went straight
//                                    if (isStraight(currentLocation, location)) {
//                                        // Update the current location
//                                        currentLocation = location
//                                    } else {
//                                        // The user didn't go straight, display an error message
//                                        println("Error: You didn't go straight!")
//                                    }
//                                }
//                            }
//                        }
//                    }
                }
                else{
                    tts = TextToSpeech(this) {
                        if (it == TextToSpeech.SUCCESS) {
                            tts.setSpeechRate(0.95f)
                            tts.speak(
                                "Destination does not exist, please shake the phone and try again!",
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                null
                            )
                        }
                    }
                }
            }
            else{
                tts = TextToSpeech(this) {
                    if (it == TextToSpeech.SUCCESS) {
                        tts.setSpeechRate(0.95f)
                        tts.speak(
                            "Start location does not exist, please shake the phone and try again!",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            null
                        )
                    }
                }
            }
        }
        else{
            tts = TextToSpeech(this) {
                if (it == TextToSpeech.SUCCESS) {
                    tts.setSpeechRate(0.95f)
                    tts.speak(
                        "Invalid direction format, please shake the phone and try again!",
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null
                    )
                }
            }
        }
    }

    private fun splitString(s: String): Pair<String, String> {
        val parts = s.lowercase().split(" to ")
        return parts[0] to parts[1]
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun sendRequest(startLoc: String, destLoc: String): JSONObject {
        return withContext(Dispatchers.IO) {
            Log.i("CHECK 2", "in fn")
            val request = URL("http://192.168.1.68:5000/sendLoc?startLoc=$startLoc&destLoc=$destLoc").openConnection() as HttpURLConnection
            Log.i("CHECK 3", "aft req")
            request.requestMethod = "POST"
            JSONObject(request.inputStream.use { it.reader().use { reader -> reader.readText() } } )
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
                            BLEScanMac = result.device.toString()
                            //println(result.device)
                            Log.i("BLE RSSI: ", result.rssi.toString())
                            //println(result.rssi)
                        }
                    }
                }
                return
            }
        }
    }

//    private fun isLeftTurn(currentLocation: String, location: String): Boolean {
//
//        if(currentLocation == "cara" && location == "student lounge"){
//            println(compassDir)
//            return compassDir == "N"
//        }
//    }
//
//    private fun isRightTurn(currentLocation: String, location: String): Boolean {
//
//        if(currentLocation == "cara" && location == "student lounge"){
//            println(compassDir)
//            return compassDir == "N"
//        }
//    }
}