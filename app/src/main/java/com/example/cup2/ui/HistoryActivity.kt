package com.example.cup2.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cup2.MainActivity
import com.example.cup2.MockInterviewActivity
import com.example.cup2.TechnicalActivity
import com.example.cup2.HrActivity
import com.example.cup2.R
import com.example.cup2.database.DatabaseHelper
import com.example.cup2.ui.general.GeneralQuestionsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HistoryActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.historyRecyclerView)
        emptyView = findViewById(R.id.emptyView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        loadHistory()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_history

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
                R.id.nav_history -> true
                R.id.nav_interview_xp -> {
                    startActivity(Intent(this, TechnicalActivity::class.java))
                    true
                }
//                R.id.nav_faq -> {
//                    startActivity(Intent(this, HrActivity::class.java))
//                    true
//                }
                else -> false
            }
        }
    }

    private fun loadHistory() {
        val historyList = dbHelper.getAllResponses()
        if (historyList.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            recyclerView.adapter = HistoryAdapter(historyList)
        }
    }
}
