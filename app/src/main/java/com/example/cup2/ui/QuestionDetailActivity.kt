package com.example.cup2.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cup2.R
import android.widget.TextView

class QuestionDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_detail)

        val question = intent.getStringExtra("question")
        findViewById<TextView>(R.id.questionTextView).text = question
    }
}
