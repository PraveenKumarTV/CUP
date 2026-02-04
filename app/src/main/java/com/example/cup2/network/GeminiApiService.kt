package com.example.cup2.network
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


object GeminiApiService {
    private val client= OkHttpClient()
    fun evaluateAnswer(apiKey:String,question:String,answer:String,callback:(String)->Unit){
        val url= "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=\$apiKey"
        val prompt= """Question: $question User Answer: $answer Evaluate the answer. Give feedback and correctness.""".trimIndent()

        val jsonBody = JSONObject().apply{
            put("contents", listOf(mapOf("parts" to listOf(mapOf("text" to prompt)))))
        }
        val body=jsonBody.toString().toRequestBody("application/json".toMediaType())
        val request=Request.Builder()
            .url(url)
            .post(body)
            .build()
        client.newCall(request).enqueue(object:Callback{
            override fun onFailure(call:Call,e:IOException){
                callback("Error:${e.localizedMessage}")
            }
            override fun onResponse(call:Call,response:Response){
                val responseBody=response.body?.string()
                if(!response.isSuccessful || responseBody==null){
                    callback("Failed to get response from AI")
                    return
                }
                try{
                    val json=JSONObject(responseBody)
                    val text=json.getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")
                    callback(text)

                }catch(e:Exception){
                    callback("Parsing error")
                }
            }
        })

    }
}