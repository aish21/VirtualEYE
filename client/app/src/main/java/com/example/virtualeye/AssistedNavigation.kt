package com.example.virtualeye

// Required Imports
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
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
import java.net.HttpURLConnection
import java.net.URL

/**
 * This class represents the Assisted Navigation screen, which provides enables the free-roam mode
 * for the user and detects the obstacles in user's path using the device's sensors.
 */

@Suppress("DEPRECATION")
class AssistedNavigation : AppCompatActivity(), SensorEventListener {

    // Declare variables and assign them later
    private lateinit var binding: AssistedNavigationBinding
    private lateinit var welcomeMsg_tts: TextToSpeech
    private lateinit var sensorMsg_tts: TextToSpeech
    private lateinit var objectDetector: ObjectDetector
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var tts: TextToSpeech
    private lateinit var sensorManager2: SensorManager
    private var mSpeechRecognizer: SpeechRecognizer? = null
    private var mIsListening = false

    // Define lists of possible start and destination locations
    val startList = listOf("cara", "student lounge", "hardware lab 1", "hardware lab 2", "software lab 1", "software lab 2", "hardware projects lab")
    val destList = listOf("cara", "student lounge", "hardware lab 1", "hardware lab 2", "software lab 1", "software lab 2", "hardware projects lab")

    companion object {
        // This constant is needed to verify the audio permission result
        private const val ASR_PERMISSION_REQUEST_CODE = 0
    }

    /**
     * This function is called when the activity is created.
     * It initializes the required objects and variables and starts the text-to-speech engine.
     * It also sets up the camera preview and object detector.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        // Initialize the text-to-speech engine for welcome message
        welcomeMsg_tts = TextToSpeech(this) {
            if (it == TextToSpeech.SUCCESS) {
                welcomeMsg_tts.setSpeechRate(0.85f)
                welcomeMsg_tts.speak(
                    "Welcome to Free Roam Mode! In this mode, you will be alerted of the obstacles around you. These are the following locations available near you - Cybercrime Analysis & Research Alliance @ NTU (CARA). SCSE Student Lounge. Hardware Lab 1. Hardware Lab 2. Software Lab 1. Software Lab 2. Hardware Projects Lab. If you would like to start navigation, kindly shake your phone!",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )
            }
        }

        // Initialize the sensor manager for accelerometer sensor
        sensorManager2 = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Inflate the layout and bind the view
        binding = DataBindingUtil.setContentView(this, R.layout.assisted_navigation)

        // Set up the camera preview
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider = cameraProvider)
        }, ContextCompat.getMainExecutor(this))

        // Set up the object detector using the pre-trained model
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

        // Initialize the text-to-speech engine for sensor messages
        sensorMsg_tts = TextToSpeech(this) {
            if (it == TextToSpeech.SUCCESS) {
                sensorMsg_tts.setSpeechRate(0.90f)
            }
        }

    }

    /**
     * Starts the microphone and toggles between speech recognition
     * start and stop based on the current state.
     */

    private fun startMicrophone() {
        verifyAudioPermissions()
        createSpeechRecognizer()

        if (mIsListening) {
            handleSpeechEnd()
        } else {
            handleSpeechBegin()
        }
    }

    /**
     * Resumes the sensor manager and registers the accelerometer sensor to detect phone shaking.
     */

    override fun onResume() {
        super.onResume()
        sensorManager2.registerListener(this,
            sensorManager2.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL)
    }

    /**
     * Pauses the sensor manager and unregisters the accelerometer sensor.
     */

    override fun onPause() {
        super.onPause()
        sensorManager2.unregisterListener(this)
    }

    /**
     * Detects the changes in the accelerometer sensor and triggers speech recognition when the phone is shaken.
     * If the acceleration is greater than 2, it indicates that the phone has been shaken.
     * It speaks a message prompting the user to provide instruction in the format - Start Location to Destination.
     * After 7 seconds, it starts the microphone for speech recognition.
     *
     * @param event the sensor event containing the accelerometer values.
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

                if(tts.isSpeaking){
                    tts.stop()
                }

                sensorMsg_tts.speak(
                    "Please provide your instruction in the format - Start Location to Destination. After the beep.",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )

                val handler = Handler()
                handler.postDelayed({
                    startMicrophone()
                }, 7000)  // 5000 milliseconds = 5 seconds
            }
        }
    }


    // Not needed - placeholder function
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed - placeholder function
    }

    /**
     * Called when the user responds to the permission request dialog.
     *
     * @param requestCode The request code passed in requestPermissions()
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions.
     * Never null. grantResults is either PackageManager.PERMISSION_GRANTED
     * or PackageManager.PERMISSION_DENIED.
     */

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

    /**
     * Binds the camera preview to the preview view and performs object detection on the captured images.
     * @param cameraProvider The ProcessCameraProvider object which provides access to the device's camera.
     */

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
                        // Iterate through the detected objects
                        for (i in objects) {
                            // Remove any previously drawn rectangles
                            if(binding.parentLayout.childCount > 1) binding.parentLayout.removeViewAt(1)

                            // Draw a rectangle around the detected object
                            val element = Draw(context = this,
                                rect = i.boundingBox,
                                text = i.labels.firstOrNull()?.text ?: "Undefined")

                            // Retrieve the object label and speak it out if it's different from the previously detected object
                            val objDet = i.labels.firstOrNull()?.text ?: "Undefined"

                            binding.parentLayout.addView(element)

                            // Speak out the detected object label
                            tts = TextToSpeech(this) {
                                if (it == TextToSpeech.SUCCESS) {
                                    if (textToSay != objDet && !tts.isSpeaking) {
                                        tts.speak(
                                            objDet + "detected",
                                            TextToSpeech.QUEUE_FLUSH,
                                            null,
                                            null
                                        )
                                        // Update the previously detected object label
                                        textToSay = objDet
                                        // Vibrate the phone to indicate a new object has been detected
                                        vibratePhone()
                                        // Display the detected object label in a toast message
                                        Toast.makeText(this, objDet + " detected" , Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }
                        // Close the imageProxy after processing all detected objects
                        imageProxy.close()
                    }.addOnFailureListener {
                        Log.v("AssistedNavigation", "ERROR - ${it.message}")
                        imageProxy.close()
                    }
            }
        }
        // Bind the camera provider to the lifecycle, camera selector, image analysis, and preview objects
        cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, imageAnalysis, preview)
    }

    /**
     * Vibrate the phone if it has a vibrator and the app has the required permission.
     */

    private fun vibratePhone() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            // Vibrator availability checking
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE)) // New vibrate method for API Level 26 or higher
            } else {
                // Vibrate method for below API Level 26
                vibrator.vibrate(500)
            }
        }
    }

    /**
     * Verify if the app has audio recording permissions. If not, request the permission.
     */

    private fun verifyAudioPermissions() {
        if (checkCallingOrSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                ASR_PERMISSION_REQUEST_CODE
            )
        }
    }

    /**
     * Create a SpeechRecognizer object and set a RecognitionListener to handle the recognition events.
     */

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

    /**
     * This method creates and returns an intent for speech recognition.
     *
     * @return Intent for speech recognition
     */

    private fun createIntent(): Intent {
        // create a new intent for speech recognition
        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        // set the language model to free form
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        // enable partial results to receive intermediate recognition results
        i.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        // set the language for speech recognition to English (India)
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
        // return the created intent
        return i
    }

    /**
     * This method handles the start of speech recognition by starting an audio session
     */

    private fun handleSpeechBegin() {
        // start audio session
        mIsListening = true
        mSpeechRecognizer!!.startListening(createIntent())
    }

    /**
     * This method handles the end of speech recognition by canceling the audio session
     */

    private fun handleSpeechEnd() {
        // end audio session
        mIsListening = false
        mSpeechRecognizer!!.cancel()
    }

    /**
     * This method handles the actions to perform once user gives instructions
     */

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

                    if(startPoint == "cara" && destLoc == "student lounge"){
                        Globals.initBearing = "S"
                    }else if (startPoint == "cara" && destLoc == "software lab 1"){
                        Globals.initBearing = "S"
                    }else if (startPoint == "cara" && destLoc == "hardware lab 1"){
                        Globals.initBearing = "S"
                    }else if (startPoint == "cara" && destLoc == "hardware lab 2"){
                        Globals.initBearing = "S"
                    }else if (startPoint == "cara" && destLoc == "software lab 2"){
                        Globals.initBearing = "S"
                    }else if (startPoint == "cara" && destLoc == "hardware projects lab"){
                        Globals.initBearing = "S"
                    }

                    else if(startPoint == "student lounge" && destLoc == "cara"){
                        Globals.initBearing = "W"
                    }else if (startPoint == "student lounge" && destLoc == "software lab 1"){
                        Globals.initBearing = "E"
                    }else if (startPoint == "student lounge" && destLoc == "hardware lab 1"){
                        Globals.initBearing = "E"
                    }else if (startPoint == "student lounge" && destLoc == "hardware lab 2"){
                        Globals.initBearing = "E"
                    }else if (startPoint == "student lounge" && destLoc == "software lab 2"){
                        Globals.initBearing = "E"
                    }else if (startPoint == "student lounge" && destLoc == "hardware projects lab"){
                        Globals.initBearing = "E"
                    }

                    else if(startPoint == "software lab 1" && destLoc == "student lounge"){
                        Globals.initBearing = "W"
                    }else if (startPoint == "software lab 1" && destLoc == "cara"){
                        Globals.initBearing = "W"
                    }else if (startPoint == "software lab 1" && destLoc == "hardware lab 1"){
                        Globals.initBearing = "E"
                    }else if (startPoint == "software lab 1" && destLoc == "hardware lab 2"){
                        Globals.initBearing = "E"
                    }else if (startPoint == "software lab 1" && destLoc == "software lab 2"){
                        Globals.initBearing = "E"
                    }else if (startPoint == "software lab 1" && destLoc == "hardware projects lab"){
                        Globals.initBearing = "E"
                    }

                    else if(startPoint == "hardware lab 1" && destLoc == "student lounge"){
                        Globals.initBearing = "S"
                    }else if (startPoint == "hardware lab 1" && destLoc == "software lab 1"){
                        Globals.initBearing = "S"
                    }else if (startPoint == "hardware lab 1" && destLoc == "cara"){
                        Globals.initBearing = "S"
                    }else if (startPoint == "hardware lab 1" && destLoc == "hardware lab 2"){
                        Globals.initBearing = "S"
                    }else if (startPoint == "hardware lab 1" && destLoc == "software lab 2"){
                        Globals.initBearing = "S"
                    }else if (startPoint == "hardware lab 1" && destLoc == "hardware projects lab"){
                        Globals.initBearing = "S"
                    }

                    else if(startPoint == "hardware lab 2" && destLoc == "student lounge"){
                        Globals.initBearing = "W"
                    }else if (startPoint == "hardware lab 2" && destLoc == "software lab 1"){
                        Globals.initBearing = "W"
                    }else if (startPoint == "hardware lab 2" && destLoc == "hardware lab 1"){
                        Globals.initBearing = "W"
                    }else if (startPoint == "hardware lab 2" && destLoc == "cara"){
                        Globals.initBearing = "W"
                    }else if (startPoint == "hardware lab 2" && destLoc == "software lab 2"){
                        Globals.initBearing = "E"
                    }else if (startPoint == "hardware lab 2" && destLoc == "hardware projects lab"){
                        Globals.initBearing = "E"
                    }

                    else if(startPoint == "software lab 2" && destLoc == "student lounge"){
                        Globals.initBearing = "S"
                    }else if (startPoint == "software lab 2" && destLoc == "software lab 1"){
                        Globals.initBearing = "S"
                    }else if (startPoint == "software lab 2" && destLoc == "hardware lab 1"){
                        Globals.initBearing = "S"
                    }else if (startPoint == "software lab 2" && destLoc == "hardware lab 2"){
                        Globals.initBearing = "S"
                    }else if (startPoint == "software lab 2" && destLoc == "cara"){
                        Globals.initBearing = "S"
                    }else if (startPoint == "software lab 2" && destLoc == "hardware projects lab"){
                        Globals.initBearing = "S"
                    }

                    else if(startPoint == "hardware projects lab" && destLoc == "student lounge"){
                        Globals.initBearing = "W"
                    }else if (startPoint == "hardware projects lab" && destLoc == "software lab 1"){
                        Globals.initBearing = "W"
                    }else if (startPoint == "hardware projects lab" && destLoc == "hardware lab 1"){
                        Globals.initBearing = "W"
                    }else if (startPoint == "hardware projects lab" && destLoc == "hardware lab 2"){
                        Globals.initBearing = "W"
                    }else if (startPoint == "hardware projects lab" && destLoc == "software lab 2"){
                        Globals.initBearing = "W"
                    }else if (startPoint == "hardware projects lab" && destLoc == "cara"){
                        Globals.initBearing = "W"
                    }

                    // Send the start and dest to server
                    GlobalScope.launch {
                        Log.i("Check", "HERE")
                        val respJSON = sendRequest(startPoint, destLoc)
                        // Update the UI or do something with the response here
                        Log.i("RESP", respJSON.toString())

                        val pathTemp = respJSON.getJSONArray("path")
                        for (i in 0 until pathTemp.length()) {
                            Globals.path.add(pathTemp.get(i) as String)
                        }

                        val directionsTemp = respJSON.getJSONArray("directions")
                        for (i in 0 until directionsTemp.length()) {
                            Globals.directions.add(directionsTemp.get(i) as String)
                        }

                        val bearingsTemp = respJSON.getJSONArray("bearings")
                        for (i in 0 until bearingsTemp.length()) {
                            Globals.bearings.add(bearingsTemp.get(i) as String)
                        }

                        Globals.startLocation = startPoint
                        Globals.destLocation = destLoc

                        println(Globals.path)
                        println(Globals.directions)
                        println(Globals.bearings)
                    }

                    Thread.sleep(5000)

                    val intentBlindNav = Intent(this, BlindNav::class.java)
                    startActivity(intentBlindNav)
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

    /**
     * Splits the input string into two parts, separated by " to " and returns a pair containing the two parts.
     * The returned pair will have the first part as the start location and the second part as the destination location.
     *
     * @param s the input string to split
     * @return a pair containing the start and destination locations
     */

    private fun splitString(s: String): Pair<String, String> {
        val parts = s.lowercase().split(" to ")
        return parts[0] to parts[1]
    }

    /**
     * Sends a request to the server to retrieve the route information for the given start and destination locations.
     *
     * @param startLoc the start location of the route
     * @param destLoc the destination location of the route
     * @return a JSONObject containing the route information
     */

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun sendRequest(startLoc: String, destLoc: String): JSONObject {
        return withContext(Dispatchers.IO) {
            val request = URL("http://4.236.138.187:5000/sendLoc?startLoc=$startLoc&destLoc=$destLoc").openConnection() as HttpURLConnection
            request.requestMethod = "POST"
            JSONObject(request.inputStream.use { it.reader().use { reader -> reader.readText() } } )
        }
    }

    /**
     * Overrides the default behavior when the back button is pressed.
     * Finishes the current activity and starts the main activity.
     */

    override fun onBackPressed() {
        finish()
        val goBackHome = Intent(this, MainActivity::class.java)
        welcomeMsg_tts.shutdown()
        tts.shutdown()
        startActivity(goBackHome)
    }

}