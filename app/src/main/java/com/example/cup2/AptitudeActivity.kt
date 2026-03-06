package com.example.cup2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.View
import com.example.cup2.ui.HistoryActivity

/**
 * AptitudeActivity - module page template.
 */
class AptitudeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aptitude)

        // Bottom nav behavior
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationApt)
        // mark current item selected
        bottomNav.selectedItemId = R.id.nav_mock_interview


        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_mock_interview -> {
                    // already here
                    true
                }
//                R.id.nav_faq -> {
//                    startActivity(Intent(this, TechnicalActivity::class.java))
//                    true
//                }
                R.id.nav_interview_xp -> {
                    startActivity(Intent(this, HrActivity::class.java))
                    true
                }
                R.id.nav_history->{
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
