package com.example.cup2.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cup2.R
import android.widget.TextView
import android.widget.EditText
import android.widget.Button
import com.example.cup2.network.GeminiApiService


class QuestionDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_detail)

        val questionTextView=findViewById<TextView>(R.id.questionTextView)
        val answerEditText=findViewById<EditText>(R.id.answerEditText)
        val submitButton=findViewById<Button>(R.id.submitButton)
        val aiResponseTextView=findViewById<TextView>(R.id.aiResponseTextView)
        val question=intent.getStringExtra("question")?:""
        questionTextView.text=question
        submitButton.setOnClickListener {
            val answer=answerEditText.text.toString().trim()
            if(answer.isEmpty()){
                aiResponseTextView.text="Please enter an answer."
                return@setOnClickListener
            }
            aiResponseTextView.text="Processing..."
            val apiKey=getString(R.string.gemini_api_key)
            GeminiApiService.evaluateAnswer(
                apiKey=apiKey,question=question,answer=answer
            ){
                response->runOnUiThread {
                aiResponseTextView.text=response
            }
            }


        }


    }
}
