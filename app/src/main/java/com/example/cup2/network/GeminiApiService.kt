package com.example.cup2.network

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object GeminiApiService {

    private const val TAG = "Gemini_DEBUG"

    // ✅ IMPORTANT: Increase timeouts (fixes timeout crashes)
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    fun evaluateAnswer(
        apiKey: String,
        question: String,
        answer: String,
        callback: (String) -> Unit
    ) {

        val url =
            "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=$apiKey"

        val prompt = """
            Question: $question
            User Answer: $answer
            Evaluate the answer. Give feedback and correctness.
        """.trimIndent()

        // -------------------------------
        // ✅ STRICT JSON (Gemini compliant)
        // -------------------------------
        val part = JSONObject()
        part.put("text", prompt)

        val partsArray = JSONArray()
        partsArray.put(part)

        val content = JSONObject()
        content.put("parts", partsArray)

        val contentsArray = JSONArray()
        contentsArray.put(content)

        val requestBodyJson = JSONObject()
        requestBodyJson.put("contents", contentsArray)

        val body = requestBodyJson
            .toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "❌ Network error", e)
                callback("Network timeout. Please try again.")
            }

            override fun onResponse(call: Call, response: Response) {
                val rawResponse = response.body?.string()

                Log.d(TAG, "HTTP CODE = ${response.code}")
                Log.d(TAG, "RAW RESPONSE = $rawResponse")

                if (!response.isSuccessful || rawResponse == null) {
                    callback("AI Error (${response.code}). Try again later.")
                    return
                }

                try {
                    val json = JSONObject(rawResponse)
                    val text =
                        json.getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text")

                    callback(text)

                } catch (e: Exception) {
                    Log.e(TAG, "❌ Parsing error", e)
                    callback("Response parsing failed.")
                }
            }
        })
    }
}
