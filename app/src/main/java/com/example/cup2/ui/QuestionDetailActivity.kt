package com.example.cup2.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cup2.R
import com.example.cup2.database.DatabaseHelper
import com.example.cup2.network.GeminiApiService

class QuestionDetailActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private var lastAiResponse: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_detail)

        dbHelper = DatabaseHelper(this)

        val questionTextView = findViewById<TextView>(R.id.questionTextView)
        val answerEditText = findViewById<EditText>(R.id.answerEditText)
        val submitButton = findViewById<Button>(R.id.submitButton)
        val saveResponseButton = findViewById<Button>(R.id.saveResponseButton)
        val aiResponseTextView = findViewById<TextView>(R.id.aiResponseTextView)
        val question = intent.getStringExtra("question") ?: ""
        questionTextView.text = question

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
}
