package com.example.cup2.ui

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cup2.R
import com.example.cup2.database.DatabaseHelper
import com.example.cup2.network.GeminiApiService
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QuestionDetailActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private var lastAiResponse: String? = null
    private lateinit var answerEditText: EditText
    private lateinit var speechRecognizer: SpeechRecognizer
    private var isListening = false

    private lateinit var viewFinder: PreviewView
    private lateinit var videoView: VideoView
    private var recording: Recording? = null
    private lateinit var videoCapture: VideoCapture<Recorder>
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_detail)

        // Initialize Ads
        MobileAds.initialize(this) {}
        val adView = findViewById<AdView>(R.id.adView)
        adView.loadAd(AdRequest.Builder().build())

        dbHelper = DatabaseHelper(this)
        cameraExecutor = Executors.newSingleThreadExecutor()

        val questionTextView = findViewById<TextView>(R.id.questionTextView)
        answerEditText = findViewById(R.id.answerEditText)
        val submitButton = findViewById<Button>(R.id.submitButton)
        val saveResponseButton = findViewById<Button>(R.id.saveResponseButton)
        val aiResponseTextView = findViewById<TextView>(R.id.aiResponseTextView)
        val startMicButton = findViewById<Button>(R.id.startMicButton)
        val stopMicButton = findViewById<Button>(R.id.stopMicButton)
        viewFinder = findViewById(R.id.viewFinder)
        videoView = findViewById(R.id.videoView)

        val question = intent.getStringExtra("question") ?: ""
        questionTextView.text = question

        // Setup Speech Recognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { if (isListening) speechRecognizer.startListening(speechIntent) }
            override fun onError(error: Int) { if (isListening) speechRecognizer.startListening(speechIntent) }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val currentText = answerEditText.text.toString()
                    val newText = if (currentText.isEmpty()) matches[0] else "$currentText ${matches[0]}"
                    answerEditText.setText(newText)
                    answerEditText.setSelection(answerEditText.text.length)
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        // Initialize CameraX Video Capture
        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
            .build()
        videoCapture = VideoCapture.withOutput(recorder)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        startMicButton.setOnClickListener {
            if (allPermissionsGranted()) {
                startListening(speechIntent)
                startRecording()
            } else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            }
        }

        stopMicButton.setOnClickListener {
            stopListening()
            stopRecording()
        }

        submitButton.setOnClickListener {
            if (isListening) {
                stopListening()
                stopRecording()
            }
            val answer = answerEditText.text.toString().trim()
            if (answer.isEmpty()) {
                aiResponseTextView.text = getString(R.string.please_enter_answer) // Make sure to define this in strings.xml or use a hardcoded string if not defined
                return@setOnClickListener
            }
            aiResponseTextView.text = "Processing..."
            saveResponseButton.visibility = View.GONE
            val apiKey = getString(R.string.gemini_api_key)
            GeminiApiService.evaluateAnswer(apiKey, question, answer) { response ->
                runOnUiThread {
                    val formattedResponse = response.replace("\\*\\*(.*?)\\*\\*".toRegex(), "<b>$1</b>")
                        .replace("*", "")
                        .replace("(\\d+)\\.".toRegex(), "<br><br>$1. ")
                        .trim()
                    aiResponseTextView.text = android.text.Html.fromHtml(formattedResponse, android.text.Html.FROM_HTML_MODE_LEGACY)
                    lastAiResponse = response
                    saveResponseButton.visibility = View.VISIBLE
                }
            }
        }

        saveResponseButton.setOnClickListener {
            val answer = answerEditText.text.toString().trim()
            val response = lastAiResponse
            if (response != null && answer.isNotEmpty()) {
                if (dbHelper.addResponse(question, answer, response) != -1L) {
                    Toast.makeText(this, "Response saved to history", Toast.LENGTH_SHORT).show()
                    saveResponseButton.visibility = View.GONE
                }
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, videoCapture)
            } catch (exc: Exception) {
                Toast.makeText(this, "Camera binding failed", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun startRecording() {
        val name = "CUP-Mock-" + SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CUP-Videos")
            }
        }
        val mediaStoreOutputOptions = MediaStoreOutputOptions.Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues).build()

        recording = videoCapture.output
            .prepareRecording(this, mediaStoreOutputOptions)
            .withAudioEnabled()
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                if (recordEvent is VideoRecordEvent.Finalize) {
                    if (!recordEvent.hasError()) {
                        runOnUiThread {
                            viewFinder.visibility = View.GONE
                            videoView.visibility = View.VISIBLE
                            videoView.setVideoURI(recordEvent.outputResults.outputUri)
                            videoView.start()
                            Toast.makeText(this, "Review your mock video above!", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        recording = null
                        Toast.makeText(this, "Video recording error: ${recordEvent.error}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun stopRecording() {
        recording?.stop()
        recording = null
    }

    private fun startListening(intent: Intent) {
        isListening = true
        findViewById<Button>(R.id.startMicButton).visibility = View.GONE
        findViewById<Button>(R.id.stopMicButton).visibility = View.VISIBLE
        speechRecognizer.startListening(intent)
    }

    private fun stopListening() {
        isListening = false
        findViewById<Button>(R.id.startMicButton).visibility = View.VISIBLE
        findViewById<Button>(R.id.stopMicButton).visibility = View.GONE
        speechRecognizer.stopListening()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }
}
