package com.example.cup2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * TechnicalActivity - module page.
 */
class TechnicalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_technical)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationTech)
        bottomNav.selectedItemId = R.id.nav_technical

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_aptitude -> {
                    startActivity(Intent(this, AptitudeActivity::class.java))
                    true
                }
                R.id.nav_technical -> true
                R.id.nav_hr -> {
                    startActivity(Intent(this, HrActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
