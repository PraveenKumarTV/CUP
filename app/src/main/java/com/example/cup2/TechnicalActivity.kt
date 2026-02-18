package com.example.cup2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.cup2.ui.general.GeneralQuestionsActivity

class TechnicalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_technical)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationTech)

        // This screen corresponds to Interview Experience
        bottomNav.selectedItemId = R.id.nav_interview_xp

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }

                R.id.nav_mock_interview -> {
                    startActivity(Intent(this, MockInterviewActivity::class.java))
                    true
                }

                R.id.nav_practice -> {
                    startActivity(Intent(this, GeneralQuestionsActivity::class.java))
                    true
                }

                R.id.nav_interview_xp -> {
                    true // Already here
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
