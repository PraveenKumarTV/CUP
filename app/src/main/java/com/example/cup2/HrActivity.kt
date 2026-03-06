package com.example.cup2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cup2.ui.HistoryActivity
import com.example.cup2.ui.general.GeneralQuestionsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HrActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hr)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationHr)

        // Set currently selected item (FAQ since HR = FAQ in your menu)
        //bottomNav.selectedItemId = R.id.nav_faq

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }

                R.id.nav_mock_interview -> {
                    startActivity(Intent(this, AptitudeActivity::class.java))
                    true
                }

                R.id.nav_practice -> {
                    startActivity(Intent(this, GeneralQuestionsActivity::class.java))
                    true
                }

                R.id.nav_interview_xp -> {
                    startActivity(Intent(this, TechnicalActivity::class.java))
                    true
                }
                R.id.nav_history->{
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }

//                R.id.nav_faq -> {
//                    true // Already in HR/FAQ screen
//                }

                else -> false
            }
        }
    }
}
