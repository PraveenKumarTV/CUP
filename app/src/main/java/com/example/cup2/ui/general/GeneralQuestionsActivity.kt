package com.example.cup2.ui.general

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
import com.example.cup2.model.SheetsResponse
import com.example.cup2.model.Question










class GeneralQuestionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_general_questions)
        val recycler=findViewById<RecyclerView>(R.id.questionsRecycler)
        recycler.layoutManager=LinearLayoutManager(this)
        val retrofit=Retrofit.Builder()
            .baseUrl("https://sheets.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api=retrofit.create(SheetsApi::class.java)
        lifecycleScope.launch{
            try {
                val response = api.getQuestions(
                    sheetId = "16hq9mEvGkRmCWlQpOi5RjFgytvetwJAZmeam6Snb1oU",
                    apiKey = "AIzaSyAj-ZZH-TTTC8rZEwYLHXZVFuSbslbzgNc"
                )
                val questions = response.values.map { it[0] }
                recycler.adapter = QuestionsAdapter(questions)
            }catch(e:Exception){
                e.printStackTrace()
            }
        }
    }
}