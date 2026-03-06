package com.example.cup2.ui

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_detail)

        dbHelper = DatabaseHelper(this)

        val questionTextView = findViewById<TextView>(R.id.questionTextView)
        val answerEditText = findViewById<EditText>(R.id.answerEditText)
        val submitButton = findViewById<Button>(R.id.submitButton)
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
            val apiKey = getString(R.string.gemini_api_key)
            GeminiApiService.evaluateAnswer(
                apiKey = apiKey, question = question, answer = answer
            ) { response ->
                runOnUiThread {
                    aiResponseTextView.text = response
                    // Save to database
                    val id = dbHelper.addResponse(question, answer, response)
                    if (id != -1L) {
                        Toast.makeText(this, "Response saved to history", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to save response", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
