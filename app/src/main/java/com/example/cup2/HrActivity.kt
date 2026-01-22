package com.example.cup2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * HrActivity - module page.
 */
class HrActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hr)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationHr)
        bottomNav.selectedItemId = R.id.nav_hr

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_aptitude -> {
                    startActivity(Intent(this, AptitudeActivity::class.java))
                    true
                }
                R.id.nav_technical -> {
                    startActivity(Intent(this, TechnicalActivity::class.java))
                    true
                }
                R.id.nav_hr -> true
                else -> false
            }
        }
    }
}
