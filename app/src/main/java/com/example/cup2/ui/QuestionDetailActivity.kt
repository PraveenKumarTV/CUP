package com.example.cup2.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.cup2.R
import com.example.cup2.database.DatabaseHelper
import com.example.cup2.network.GeminiApiService
import java.util.Locale

class QuestionDetailActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private var lastAiResponse: String? = null
    private lateinit var answerEditText: EditText

    private val speechResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val res = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = res?.get(0)
            if (!spokenText.isNullOrEmpty()) {
                val currentText = answerEditText.text.toString()
                if (currentText.isNotEmpty()) {
                    answerEditText.setText("$currentText $spokenText")
                } else {
                    answerEditText.setText(spokenText)
                }
                answerEditText.setSelection(answerEditText.text.length)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_detail)

        dbHelper = DatabaseHelper(this)

        val questionTextView = findViewById<TextView>(R.id.questionTextView)
        answerEditText = findViewById(R.id.answerEditText)
        val submitButton = findViewById<Button>(R.id.submitButton)
        val saveResponseButton = findViewById<Button>(R.id.saveResponseButton)
        val aiResponseTextView = findViewById<TextView>(R.id.aiResponseTextView)
        val micButton = findViewById<ImageButton>(R.id.micButton)
        
        val question = intent.getStringExtra("question") ?: ""
        questionTextView.text = question

        micButton.setOnClickListener {
            startSpeechToText()
        }

        submitButton.setOnClickListener {
            val answer = answerEditText.text.toString().trim()
            if (answer.isEmpty()) {
                aiResponseTextView.text = "Please enter an answer."
                return@setOnClickListener
            }
            aiResponseTextView.text = "Processing..."
            saveResponseButton.visibility = View.GONE
            val apiKey = getString(R.string.gemini_api_key)
            GeminiApiService.evaluateAnswer(
                apiKey = apiKey, question = question, answer = answer
            ) { response ->
                runOnUiThread {
                    var formattedResponse = response ?: ""

                    // Convert **bold** to HTML bold
                    formattedResponse = formattedResponse.replace("\\*\\*(.*?)\\*\\*".toRegex(), "<b>$1</b>")

                    // Remove remaining *
                    formattedResponse = formattedResponse.replace("*", "")

                    // Add gap before numbered points
                    formattedResponse = formattedResponse.replace("(\\d+)\\.".toRegex(), "<br><br>$1. ")

                    formattedResponse = formattedResponse.trim()

                    aiResponseTextView.text =
                        android.text.Html.fromHtml(formattedResponse, android.text.Html.FROM_HTML_MODE_LEGACY)

                    lastAiResponse = response
                    saveResponseButton.visibility = View.VISIBLE
                }
            }
        }

        saveResponseButton.setOnClickListener {
            val answer = answerEditText.text.toString().trim()
            val response = lastAiResponse
            if (response != null && answer.isNotEmpty()) {
                val id = dbHelper.addResponse(question, answer, response)
                if (id != -1L) {
                    Toast.makeText(this, "Response saved to history", Toast.LENGTH_SHORT).show()
                    saveResponseButton.visibility = View.GONE
                } else {
                    Toast.makeText(this, "Failed to save response", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your answer...")
        try {
            speechResultLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Speech recognition not supported on this device", Toast.LENGTH_SHORT).show()
        }
    }
}
