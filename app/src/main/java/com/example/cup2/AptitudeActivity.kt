package com.example.cup2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.View

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
        bottomNav.selectedItemId = R.id.nav_aptitude

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_aptitude -> {
                    // already here
                    true
                }
                R.id.nav_technical -> {
                    startActivity(Intent(this, TechnicalActivity::class.java))
                    true
                }
                R.id.nav_hr -> {
                    startActivity(Intent(this, HrActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
