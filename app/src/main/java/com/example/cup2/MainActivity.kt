package com.example.cup2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.View
import android.widget.Toast

/**
 * MainActivity - Home screen with banner, module cards and bottom navigation.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Uses Theme.PlacementPrep from themes.xml
        setContentView(R.layout.activity_main)

        // Module card click listeners
        findViewById<View>(R.id.cardAptitude).setOnClickListener {
            startActivity(Intent(this, AptitudeActivity::class.java))
        }
        findViewById<View>(R.id.cardTechnical).setOnClickListener {
            startActivity(Intent(this, TechnicalActivity::class.java))
        }
        findViewById<View>(R.id.cardHr).setOnClickListener {
            startActivity(Intent(this, HrActivity::class.java))
        }

        // Bottom navigation setup
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        // Default nothing selected on home (or you can set a home item if desired)
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
                R.id.nav_hr -> {
                    startActivity(Intent(this, HrActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
