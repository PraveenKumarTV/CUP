package com.example.cup2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class MockInterviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mock_interview)

        val generalQuestionsCard = findViewById<MaterialCardView>(R.id.generalQuestionsCard)
        generalQuestionsCard.setOnClickListener {
            startActivity(Intent(this, GeneralQuestionsActivity::class.java))
        }
    }
}