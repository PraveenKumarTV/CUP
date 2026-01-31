package com.example.cup2.worker

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.cup2.network.SheetsApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.cup2.utils.SheetPrefs
import com.example.cup2.notifications.NotificationHelper

class SheetCheckoutWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        return try {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://sheets.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(SheetsApi::class.java)

            val response = api.getQuestions(
                sheetId = "16hq9mEvGkRmCWlQpOi5RjFgytvetwJAZmeam6Snb1oU",
                apiKey = "AIzaSyAj-ZZH-TTTC8rZEwYLHXZVFuSbslbzgNc"
            )

            val currentCount = response.values.size
            val lastCount = SheetPrefs.getLastCount(applicationContext)

            if (currentCount > lastCount) {
                NotificationHelper.showQuestionNotification(
                    applicationContext,
                    "New Question Available",
                    "Click here to view the latest question"
                )
                SheetPrefs.saveLastCount(applicationContext, currentCount)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
