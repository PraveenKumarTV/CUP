package com.example.cup2.ui.general

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cup2.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.cup2.network.SheetsApi
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.example.cup2.ui.general.QuestionsAdapter
import com.example.cup2.notifications.NotificationHelper
import com.example.cup2.utils.SheetPrefs
import java.lang.Exception
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.cup2.MainActivity
import com.example.cup2.MockInterviewActivity
import com.example.cup2.TechnicalActivity
import com.example.cup2.HrActivity
import com.example.cup2.ui.HistoryActivity

class GeneralQuestionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_general_questions)

        // Notification channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationHelper.createNotificationChannel(this)
        }

        // RecyclerView setup
        val recycler = findViewById<RecyclerView>(R.id.questionsRecycler)
        recycler.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://sheets.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(SheetsApi::class.java)

        lifecycleScope.launch {
            try {
                val response = api.getQuestions(
                    sheetId = "16hq9mEvGkRmCWlQpOi5RjFgytvetwJAZmeam6Snb1oU",
                    apiKey = "AIzaSyAj-ZZH-TTTC8rZEwYLHXZVFuSbslbzgNc"
                )

                val questions = response.values.map { it[0] }

                recycler.adapter = QuestionsAdapter(questions)

                SheetPrefs.saveLastCount(this@GeneralQuestionsActivity, questions.size)

                NotificationHelper.showQuestionNotification(
                    this@GeneralQuestionsActivity,
                    title = "New Question Available",
                    message = "Click here to view the latest question"
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Bottom Navigation Setup
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNav.selectedItemId = R.id.nav_practice

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
                    true // Already here
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
//                    startActivity(Intent(this, HrActivity::class.java))
//                    true
//                }

                else -> false
            }
        }
    }
}
