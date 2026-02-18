package com.example.cup2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cup2.ui.general.GeneralQuestionsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView

class MockInterviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mock_interview)

        // Card click
        val generalQuestionsCard = findViewById<MaterialCardView>(R.id.generalQuestionsCard)
        generalQuestionsCard.setOnClickListener {
            startActivity(Intent(this, GeneralQuestionsActivity::class.java))
        }

        // Bottom Navigation Setup
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)


        bottomNav.selectedItemId = R.id.nav_mock_interview

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }

                R.id.nav_mock_interview -> {
                    true // Already on this screen
                }

                R.id.nav_practice -> {
                    startActivity(Intent(this, GeneralQuestionsActivity::class.java))
                    true
                }

                R.id.nav_interview_xp -> {
                    startActivity(Intent(this, TechnicalActivity::class.java))
                    true
                }

                R.id.nav_faq -> {
                    startActivity(Intent(this, HrActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }
}
